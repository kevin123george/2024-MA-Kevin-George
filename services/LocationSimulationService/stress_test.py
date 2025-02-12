import requests
import matplotlib.pyplot as plt
from matplotlib.ticker import FormatStrFormatter

# 1. Fetch JSON data from your endpoint (or load it directly)
url = "http://localhost:9099/api/trilateration/compute/all/test/csv"
response = requests.get(url)
data = response.json()

# 2. Extract beacons and devices
beacons = data["allBeaconsUsed"]
devices = data["devices"]

# Extract beacon coordinates and names
beacon_lons = [float(b["beaconLongitude"]) for b in beacons]
beacon_lats = [float(b["beaconLatitude"]) for b in beacons]
beacon_names = [b["beaconName"] for b in beacons]

# Extract device coordinates and names
device_lons = [float(d["longitude"]) for d in devices]
device_lats = [float(d["latitude"]) for d in devices]
device_names = [d["deviceName"] for d in devices]

# Extract closest beacon coordinates
closest_beacon_lons = [float(d["closestBeacon"]["beaconLongitude"]) for d in devices]
closest_beacon_lats = [float(d["closestBeacon"]["beaconLatitude"]) for d in devices]
closest_device_lons = [float(d["closestBeacon"]["deviceLongitude"]) for d in devices]
closest_device_lats = [float(d["closestBeacon"]["deviceLatitude"]) for d in devices]
closest_beacon_names = [d["closestBeacon"]["beaconName"] for d in devices]

# Add new points
new_lon = 12.199211706650317
new_lat = 49.6816259139099

# Create figure and scatter plot
plt.figure(figsize=(10, 8))

# Plot existing points
plt.scatter(beacon_lons, beacon_lats, marker='^', color='red', label='Beacons')
plt.scatter(device_lons, device_lats, marker='o', color='blue', label='Devices')
plt.scatter(closest_device_lons, closest_device_lats, marker='s', color='green', label='Closest Beacon')

# Plot new point
plt.scatter(new_lon, new_lat, marker='*', color='purple', s=100, label='New Point')

# Plot lines connecting closest beacons to devices
for d_lon, d_lat, cb_lon, cb_lat in zip(closest_device_lons, closest_device_lats, closest_beacon_lons, closest_beacon_lats):
    plt.plot([d_lon, cb_lon], [d_lat, cb_lat], 'g--', alpha=0.6)

# Annotate points with original precision
for lon, lat, name in zip(beacon_lons, beacon_lats, beacon_names):
    plt.text(lon, lat, f"{name}\n({lon}, {lat})", fontsize=8, color="red", alpha=0.7)

for lon, lat, name in zip(device_lons, device_lats, device_names):
    plt.text(lon, lat, f"{name}\n({lon}, {lat})", fontsize=8, color="blue", alpha=0.7)

for lon, lat, name in zip(closest_device_lons, closest_device_lats, closest_beacon_names):
    plt.text(lon, lat, f"{name}\n({lon}, {lat})", fontsize=8, color="green", alpha=0.7)

# Annotate new point
plt.text(new_lon, new_lat, f"New Point\n({new_lon}, {new_lat})",
         fontsize=8, color="purple", alpha=0.7)

# Enable grid lines
plt.grid(True)

# Format axes without forcing precision
ax = plt.gca()
ax.xaxis.set_major_formatter(FormatStrFormatter('%f'))
ax.yaxis.set_major_formatter(FormatStrFormatter('%f'))

# Label axes
plt.title("Trilateration on a Grid with Closest Beacons")
plt.xlabel("Longitude")
plt.ylabel("Latitude")
plt.legend()

# Show the plot
plt.show()
