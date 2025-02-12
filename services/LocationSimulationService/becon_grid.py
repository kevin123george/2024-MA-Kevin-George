import pandas as pd
import folium

# Load the CSV file
file_path = "becons_2.csv"  # Update with the correct path
df = pd.read_csv(file_path)

# Function to convert coordinate strings to float
def convert_coordinates(coord):
    """Convert coordinate string to a float, removing '° N' or '° E'."""
    try:
        return float(coord.replace("° N", "").replace("° E", "").strip())
    except ValueError:
        return None

# Clean the latitude and longitude columns
df["Latitude"] = df["Latitude"].apply(convert_coordinates)
df["Longitude"] = df["Longitude"].apply(convert_coordinates)

# Filter for valid coordinates
valid_coordinates = df[(df["Latitude"].notna()) & (df["Longitude"].notna()) &
                       (df["Latitude"] != 0) & (df["Longitude"] != 0)]

# Determine the center of the map
map_center = [valid_coordinates["Latitude"].mean(), valid_coordinates["Longitude"].mean()]

# Create a folium map centered at the mean location with maximum zoom
m = folium.Map(location=map_center, zoom_start=22, tiles="cartodbpositron")  # High-res tiles

# Alternative for Google Satellite View:
# m = folium.Map(location=map_center, zoom_start=22, tiles="Stamen Toner")

# Add beacon markers to the map
for _, row in valid_coordinates.iterrows():
    folium.Marker(
        location=[row["Latitude"], row["Longitude"]],
        popup=f"Beacon: {row['Name']}",
        tooltip=row["Name"]
    ).add_to(m)

# Save the map as an HTML file
map_file = "beacon_map.html"
m.save(map_file)

print(f"Map has been saved as {map_file}. Open it in a browser to view the locations.")
