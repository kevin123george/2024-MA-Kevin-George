import requests
import folium

# Fetch JSON data from your endpoint (or load it directly)
url = "http://localhost:9099/api/trilateration/compute/all/test/csv"
response = requests.get(url)
data = response.json()

# Extract beacons and devices
beacons = data["allBeaconsUsed"]
devices = data["devices"]

# Extract beacon coordinates and names from allBeaconsUsed
beacon_lons = [float(b["beaconLongitude"]) for b in beacons]
beacon_lats = [float(b["beaconLatitude"]) for b in beacons]
beacon_names = [b["beaconName"] for b in beacons]

# Extract closest beacon coordinates (should be plotted using deviceLatitude and deviceLongitude)
closest_beacon_lons = [float(d["closestBeacon"]["deviceLongitude"]) for d in devices]
closest_beacon_lats = [float(d["closestBeacon"]["deviceLatitude"]) for d in devices]
closest_beacon_names = [d["closestBeacon"]["beaconName"] for d in devices]

# Initialize a Folium map centered around the average latitude and longitude with higher zoom
center_lat = sum(beacon_lats + closest_beacon_lats) / (len(beacon_lats) + len(closest_beacon_lats))
center_lon = sum(beacon_lons + closest_beacon_lons) / (len(beacon_lons) + len(closest_beacon_lons))

m = folium.Map(
    location=[center_lat, center_lon],
    zoom_start=20,   # Higher zoom level
    max_zoom=25      # Increase max zoom level
)

# Function to generate Google Maps link
def google_maps_link(lat, lon):
    return f'<a href="https://www.google.com/maps/search/?api=1&query={lat},{lon}" target="_blank">Open in Google Maps</a>'

# Add beacons to the map with Google Maps link (from allBeaconsUsed)
for lon, lat, name in zip(beacon_lons, beacon_lats, beacon_names):
    folium.Marker(
        location=[lat, lon],
        popup=f"Beacon: {name}<br>({lon}, {lat})<br>{google_maps_link(lat, lon)}",
        icon=folium.Icon(color="red", icon="info-sign")
    ).add_to(m)

# Add closest beacons to the map with Google Maps link (from closestBeacon with device lat/lon)
for lon, lat, name in zip(closest_beacon_lons, closest_beacon_lats, closest_beacon_names):
    folium.Marker(
        location=[lat, lon],
        popup=f"Closest Beacon: {name}<br>({lon}, {lat})<br>{google_maps_link(lat, lon)}",
        icon=folium.Icon(color="green", icon="star")
    ).add_to(m)

# Save and show the map
m.save("trilateration_map.html")
print("Map has been saved as 'trilateration_map.html'. Open it in a browser.")
