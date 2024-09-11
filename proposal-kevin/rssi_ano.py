import requests
import json
import matplotlib.pyplot as plt
from datetime import datetime
import numpy as np

# API endpoint
API_URL = "http://localhost:8080/api/positions"

# Function to fetch data from the API
def fetch_data():
    try:
        response = requests.get(API_URL)
        if response.status_code == 200:
            return json.loads(response.text)
        else:
            print(f"Failed to fetch data. Status code: {response.status_code}")
            return None
    except Exception as e:
        print(f"Error fetching data: {e}")
        return None

# Function to analyze RSSI for anomalies
def analyze_rssi(data):
    anomalies = []
    for entry in data:
        rssi = entry.get('rssi', None)
        if rssi is not None:
            if rssi < -80:  # Anomaly threshold for weak RSSI
                anomalies.append({
                    'deviceId': entry['deviceId'],
                    'deviceName': entry['deviceName'],
                    'rssi': rssi,
                    'fixTime': entry['fixTime']
                })
    return anomalies

# Function to visualize cattle positions on a scatter plot
def visualize_positions(data):
    latitudes = []
    longitudes = []
    rssi_values = []

    for entry in data:
        if entry['latitude'] != 0.0 and entry['longitude'] != 0.0:  # Ignore zero values
            latitudes.append(entry['latitude'])
            longitudes.append(entry['longitude'])
            rssi_values.append(entry['rssi'])

    plt.figure(figsize=(10, 6))
    plt.scatter(longitudes, latitudes, c=rssi_values, cmap='viridis', s=100, alpha=0.75)
    plt.colorbar(label="RSSI")
    plt.title('Cattle Position Based on RSSI')
    plt.xlabel('Longitude')
    plt.ylabel('Latitude')
    plt.show()

# Main script execution
if __name__ == "__main__":
    data = fetch_data()

    if data:
        print(f"Fetched {len(data)} records from the API.")

        # Anomaly Detection
        anomalies = analyze_rssi(data)
        if anomalies:
            print("Detected anomalies in RSSI values:")
            for anomaly in anomalies:
                print(f"Device: {anomaly['deviceName']} (ID: {anomaly['deviceId']}) - RSSI: {anomaly['rssi']} at {anomaly['fixTime']}")
        else:
            print("No anomalies detected in RSSI values.")

        # Visualization
        visualize_positions(data)
    else:
        print("No data to analyze.")
