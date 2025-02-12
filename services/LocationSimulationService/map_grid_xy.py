import pandas as pd
import folium
import matplotlib.pyplot as plt
import matplotlib.patches as patches
import requests
import numpy as np

# ------------------ Helper Function: Draw Rectangle ------------------ #
def draw_rectangle(ax, x1, y1, x2, y2, edgecolor='red', linewidth=2, facecolor='none', alpha=1.0, label=None):
    """
    Draws a rectangle on the given axes based on two opposite corner coordinates.
    """
    lower_left_x = min(x1, x2)
    lower_left_y = min(y1, y2)
    width = abs(x2 - x1)
    height = abs(y2 - y1)

    rect = patches.Rectangle(
        (lower_left_x, lower_left_y), width, height,
        edgecolor=edgecolor, linewidth=linewidth, facecolor=facecolor, label=label
    )
    rect.set_alpha(alpha)
    ax.add_patch(rect)
    return rect

# ------------------ 0) Load and Process Beacon Data ------------------ #
file_path = "becons_2.csv"
df = pd.read_csv(file_path)

def convert_coordinates(coord):
    """Convert coordinate string (e.g. '43.1234° N') to a float."""
    try:
        return float(coord.replace("° N", "").replace("° E", "").strip())
    except Exception:
        return None

df["Latitude"] = df["Latitude"].apply(convert_coordinates)
df["Longitude"] = df["Longitude"].apply(convert_coordinates)

# Filter valid coordinates (non-NaN, non-zero)
valid_coordinates = df[
    (df["Latitude"].notna()) & (df["Longitude"].notna()) &
    (df["Latitude"] != 0) & (df["Longitude"] != 0)
]

# Determine map center
map_center = [
    valid_coordinates["Latitude"].mean(),
    valid_coordinates["Longitude"].mean()
]

# ------------------ 1) Create the Base Folium Map and Matplotlib Plot ------------------ #
# Create a Folium map with beacon markers
m = folium.Map(location=map_center, zoom_start=24, tiles="cartodbpositron")
for _, row in valid_coordinates.iterrows():
    folium.Marker(
        location=[row["Latitude"], row["Longitude"]],
        popup=f"Beacon: {row['Name']}",
        tooltip=row["Name"]
    ).add_to(m)

# Create a Matplotlib scatter plot for beacons
plt.figure(figsize=(10, 8))
plt.scatter(
    valid_coordinates["Longitude"],
    valid_coordinates["Latitude"],
    alpha=0.7,
    c="blue",
    label="Beacons"
)
for _, row in valid_coordinates.iterrows():
    plt.text(
        row["Longitude"],
        row["Latitude"],
        str(row["Name"]),
        fontsize=8,
        ha='left',
        va='bottom'
    )

# ------------------ 2) Loop Over Minutes, Alpha and Beta Values and Call the API ------------------ #
# Define test parameter values
alpha_values = [0.1, 0.2]
beta_values  = [0.2]
minute_values = [2]

# Setup a colormap for marker colors (each plotted marker gets a different color)
total_iterations = len(minute_values) * len(alpha_values) * len(beta_values) * 2  # multiplied by 2 for both markers per entry
cmap = plt.get_cmap("viridis", total_iterations)
counter = 0

# API endpoint and parameters
base_url = "http://localhost:9099/api/proximity/apiTest"
entryNumber = 6
headers = {"cookie": "JSESSIONID=B145F43A7D3B48E74C39D36101674CDC"}

# Optional: store results
results = []

for minutes in minute_values:
    for alpha in alpha_values:
        for beta in beta_values:
            querystring = {
                "entry": str(entryNumber),
                "minutes": str(minutes),
                "alpha": str(alpha),
                "beta": str(beta)
            }
            try:
                response = requests.get(base_url, headers=headers, params=querystring)
                print("API call:", response.url)
                response.raise_for_status()
                api_response = response.json()
            except Exception as e:
                print(f"Error calling API for minutes={minutes}, alpha={alpha}, beta={beta}: {e}")
                continue

            # ------------------ Process Each Entry in the API Response ------------------ #
            for key, result in api_response.items():
                # Infer the method from the response
                method = result.get("Method", "Unknown Method")

                # Convert final coordinates (these should always be present)
                try:
                    final_lat = float(result["FinalLat"])
                    final_lon = float(result["FinalLon"])
                except Exception as e:
                    print(f"Error converting final coordinates for entry {key}: {e}")
                    continue

                # Record the result (optional)
                results.append({
                    "key": key,
                    "minutes": minutes,
                    "alpha": alpha,
                    "beta": beta,
                    "method": method,
                    "final_lat": final_lat,
                    "final_lon": final_lon,
                    "trilaterated_lat": result.get("TrilateratedLat"),
                    "trilaterated_lon": result.get("TrilateratedLon")
                })

                # ------------------ Plot the Final Coordinates (Fallback) ------------------ #
                color = cmap(counter)
                color_hex = '#%02x%02x%02x' % (int(color[0]*255), int(color[1]*255), int(color[2]*255))
                folium.CircleMarker(
                    location=[final_lat, final_lon],
                    radius=6,
                    color=color_hex,
                    fill=True,
                    fill_color=color_hex,
                    fill_opacity=0.9,
                    popup=f"Final ({method}, key={key}, min={minutes}, α={alpha}, β={beta})"
                ).add_to(m)
                plt.scatter(
                    final_lon,
                    final_lat,
                    s=150,
                    color=color,
                    marker="x",
                    label=f"Final ({method}, key={key}, min={minutes}, α={alpha}, β={beta})"
                )
                counter += 1

                # ------------------ Plot the Triangulated Coordinates (if available) ------------------ #
                if "TrilateratedLat" in result and "TrilateratedLon" in result:
                    try:
                        trilat_lat = float(result["TrilateratedLat"])
                        trilat_lon = float(result["TrilateratedLon"])
                    except Exception as e:
                        print(f"Error converting trilaterated coordinates for entry {key}: {e}")
                        continue
                    color = cmap(counter)
                    folium.CircleMarker(
                        location=[trilat_lat, trilat_lon],
                        radius=6,
                        color="black",  # you can also use color_hex if you prefer a matching color
                        fill=True,
                        fill_color="black",
                        fill_opacity=0.9,
                        popup=f"Triangulated ({method}, key={key}, min={minutes}, α={alpha}, β={beta})"
                    ).add_to(m)
                    plt.scatter(
                        trilat_lon,
                        trilat_lat,
                        s=150,
                        color=color,
                        marker="D",
                        label=f"Triangulated ({method}, key={key}, min={minutes}, α={alpha}, β={beta})"
                    )
                    counter += 1

# ------------------ Plot a Custom Observed Point with a 0.5-Meter Radius ------------------ #
custom_lat = 49.68160070713866
custom_lon = 12.199185950828012

observed_lat = 49.681612903512537
observed_lon = 12.199201486228596

# Convert 0.5 m to degrees (approximation: 1° ≈ 111,000 m)
radius_deg = 0.5 / 111000.0

circle = plt.Circle(
    (observed_lon, observed_lat),
    radius_deg,
    color='blue',
    fill=False,
    linestyle='--',
    linewidth=2,
    label='0.5 m radius'
)
ax = plt.gca()
ax.add_patch(circle)

# ------------------ Draw a Custom Rectangle ------------------ #
lon1, lat1 = 12.19919409, 49.68160992  # lower-left corner
lon2, lat2 = 12.19919394, 49.68160897  # upper-right corner
# draw_rectangle(ax, lon1, lat1, lon2, lat2, edgecolor='green', linewidth=0.3,
#                facecolor='blue', alpha=0.2, label='Custom Rectangle')

# ------------------ Interactive Click Logging ------------------ #
print("Interactive Click Logging: Click anywhere on the plot to log the coordinates.")
def log_click(event):
    if event.inaxes:
        print(f"Clicked at: ({event.xdata:.15f}, {event.ydata:.15f})")
cid = plt.gcf().canvas.mpl_connect('button_press_event', log_click)

# ------------------ Finalize and Save the Plots ------------------ #
map_file = "beacon_map_test.html"
m.save(map_file)
print(f"Folium map saved as {map_file}.")

plt.xlabel("Longitude")
plt.ylabel("Latitude")
plt.title("Beacon Grid with API Results (Method Inferred from Response)")
plt.legend(bbox_to_anchor=(1.05, 1), loc='upper left', fontsize='small')
plt.gca().set_aspect('equal', adjustable='box')
plt.tight_layout()
plt.savefig("beacons_longitude_latitude_test_plot.png", dpi=300)
plt.show()

plt.gcf().canvas.mpl_disconnect(cid)
