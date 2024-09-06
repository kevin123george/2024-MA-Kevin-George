import requests
import pandas as pd
from geopy.distance import geodesic
from sklearn.ensemble import IsolationForest
from datetime import datetime

# Fetch data from API
url = "http://localhost:8080/api/positions/177657222"
response = requests.get(url)
data = response.json()

# Convert data into a DataFrame
df = pd.DataFrame(data)

# Parse datetime fields
df['devicetime'] = pd.to_datetime(df['devicetime'])
df['devicetime'] = pd.to_datetime(df['devicetime'])

# Sort by time to ensure chronological order
df = df.sort_values(by='devicetime')

# Handle missing values
df = df.dropna(subset=['latitude', 'longitude'])

# Calculate distance between consecutive points
df['prev_latitude'] = df['latitude'].shift(1)
df['prev_longitude'] = df['longitude'].shift(1)

# Handle any rows that might still have NaN after the shift operation
df = df.dropna(subset=['prev_latitude', 'prev_longitude'])

df['distance'] = df.apply(lambda row: geodesic((row['latitude'], row['longitude']),
                                               (row['prev_latitude'], row['prev_longitude'])).meters, axis=1)

# Calculate time difference
df['time_diff'] = df['devicetime'].diff().dt.total_seconds()

# Calculate speed (distance / time)
df['speed'] = df['distance'] / df['time_diff']

# Handle any potential division by zero or NaN values
df = df.fillna(0)

# Use Isolation Forest for anomaly detection
model = IsolationForest(contamination=0.05)
df['anomaly'] = model.fit_predict(df[['latitude', 'longitude', 'speed']])

# Extract anomalies
anomalies = df[df['anomaly'] == -1]

# Print anomalies
print(anomalies[['devicetime', 'latitude', 'longitude', 'speed', 'anomaly']])
