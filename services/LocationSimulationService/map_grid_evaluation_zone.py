import pandas as pd
import folium
import matplotlib.pyplot as plt
from matplotlib.patches import Circle

# ------------------ 0) Load and Process Beacon Data ------------------ #
file_path = "becons_2.csv"
df = pd.read_csv(file_path)

def convert_coordinates(coord):
    """Convert coordinate string (e.g. '43.1234° N') to a float."""
    try:
        return float(coord.replace("° N", "").replace("° E", "").strip())
    except ValueError:
        return None

# Clean the latitude and longitude columns
df["Latitude"] = df["Latitude"].apply(convert_coordinates)
df["Longitude"] = df["Longitude"].apply(convert_coordinates)

# Filter for valid coordinates (non-NaN, non-zero)
valid_coordinates = df[
    (df["Latitude"].notna()) & (df["Longitude"].notna()) &
    (df["Latitude"] != 0) & (df["Longitude"] != 0)
]

# Determine the center of the map by averaging valid lat/long values
map_center = [valid_coordinates["Latitude"].mean(), valid_coordinates["Longitude"].mean()]

# ------------------ 1) Create the Base Folium Map and Matplotlib Plot ------------------ #
# Create a Folium map and add beacon markers
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

# Optionally add text labels for each beacon
for _, row in valid_coordinates.iterrows():
    plt.text(
        row["Longitude"],
        row["Latitude"],
        str(row["Name"]),
        fontsize=8,
        ha='left',
        va='bottom'
    )

# ------------------ 2) Add Two Cycles (Circles) ------------------ #
# Define the coordinates for the two cycles:
# Blue cycle (circle) at the observed point, to be labeled "t2"
observed_lat = 49.681612903512537
observed_lon = 12.199201486228596

# Green cycle (circle) at the custom point, to be labeled "a5"
custom_lat = 49.681626877419781

custom_lon = 12.199274534123294

# Convert 0.5 meters to degrees (approximation: 1° ≈ 111,000 m)
radius_deg = 0.5 / 111000.0


# Convert 0.5 meters to degrees (approximation: 1° ≈ 111,000 m)
radius_deg_a1 = 2 / 111000.0


ax = plt.gca()

# Blue circle labeled "t2"
blue_circle = Circle(
    (observed_lon, observed_lat),
    radius_deg,
    color='red',
    fill=False,
    linestyle='--',
    linewidth=2,
    label='t2'
)
ax.add_patch(blue_circle)

# Green circle labeled "a5"
green_circle = Circle(
    (custom_lon, custom_lat),
    radius_deg_a1,
    color='green',
    fill=False,
    linestyle='--',
    linewidth=2,
    label='a5'
)
ax.add_patch(green_circle)

# ------------------ 3) Interactive Click Logging ------------------ #
print("Interactive Click Logging: Click anywhere on the plot to log the coordinates.")

def log_click(event):
    if event.inaxes:
        print(f"Clicked at: ({event.xdata:.15f}, {event.ydata:.15f})")

cid = plt.gcf().canvas.mpl_connect('button_press_event', log_click)

# ------------------ 4) Finalize and Save the Plots ------------------ #
# Save the Folium map as HTML
map_file = "beacon_map_test.html"
m.save(map_file)
print(f"Folium map saved as {map_file}.")

plt.xlabel("Longitude")
plt.ylabel("Latitude")
plt.title("Beacon Grid with Additional Cycles")
plt.legend(bbox_to_anchor=(1.05, 1), loc='upper left', fontsize='small')
plt.gca().set_aspect('equal', adjustable='box')
plt.tight_layout()
plt.savefig("beacons_longitude_latitude_test_plot.png", dpi=300)
plt.show()

# Disconnect the click event handler after closing the plot
plt.gcf().canvas.mpl_disconnect(cid)
