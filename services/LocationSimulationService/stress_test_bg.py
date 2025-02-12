import requests
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.image as mpimg
from matplotlib.ticker import FormatStrFormatter
from PIL import Image

# Fetch JSON data
url = "http://localhost:9099/api/trilateration/compute/all/test/csv?minutes=5"
response = requests.get(url)
data = response.json()

# Extract beacons and devices
beacons = data["allBeaconsUsed"]
devices = data["devices"]

beacon_lons = [b["beaconLongitude"] for b in beacons]
beacon_lats = [b["beaconLatitude"] for b in beacons]
beacon_names = [b["beaconName"] for b in beacons]

device_lons = [d["longitude"] for d in devices]
device_lats = [d["latitude"] for d in devices]
device_names = [d["deviceName"] for d in devices]

# Load and resize background image
original_img = Image.open('Screenshot 2025-01-26 133608.png')
resized_width = int(original_img.width * 0.8)  # Resize to 80% of original width
resized_height = int(original_img.height * 0.8)  # Resize to 80% of original height
resized_img = original_img.resize((resized_width, resized_height), Image.LANCZOS)

# Convert PIL Image to numpy array
background_img = np.array(resized_img)

# Create figure
fig, ax = plt.subplots(figsize=(10, 8))

# Plot background image
ax.imshow(background_img, extent=[
    min(beacon_lons) - 0.0001,
    max(beacon_lons) + 0.0001,
    min(beacon_lats) - 0.0001,
    max(beacon_lats) + 0.0001
], aspect='auto', alpha=0.5)

# Plot beacons and devices
ax.scatter(beacon_lons, beacon_lats, marker='^', color='red', s=100, alpha=0.8, label='Beacons')
ax.scatter(device_lons, device_lats, marker='o', color='blue', s=100, alpha=0.6, label='Devices')

# Annotate points
for lon, lat, name in zip(beacon_lons, beacon_lats, beacon_names):
    ax.text(lon, lat, f"{name}\n({lon:.6f}, {lat:.6f})",
             fontsize=8, color="red", alpha=0.9,
             bbox=dict(facecolor='white', edgecolor='none', alpha=0.5))

for lon, lat, name in zip(device_lons, device_lats, device_names):
    ax.text(lon, lat, f"{name}\n({lon:.6f}, {lat:.6f})",
             fontsize=8, color="blue", alpha=0.9,
             bbox=dict(facecolor='white', edgecolor='none', alpha=0.5))

# Formatting
ax.grid(True, linestyle='--', linewidth=0.5, alpha=0.5)
ax.xaxis.set_major_formatter(FormatStrFormatter('%.6f'))
ax.yaxis.set_major_formatter(FormatStrFormatter('%.6f'))
plt.title("Trilateration with Resized Background Image")
plt.xlabel("Longitude")
plt.ylabel("Latitude")
plt.legend()
plt.tight_layout()

plt.show()