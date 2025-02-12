import requests
import matplotlib.pyplot as plt
from collections import Counter

def check_expected_beacon(entry_number, minutes, expected_beacons):
    url = f"http://localhost:9099/api/proximity/compute/all/test/csv/v2?entryNumber={entry_number}&minutes={minutes}"
    try:
        response = requests.get(url)
        response.raise_for_status()
        data = response.json()

        if not data:
            return 0  # No data, treat as failure

        # Count occurrences of each beacon
        beacon_counts = Counter(entry["Closest Beacon"] for entry in data.values())

        # Find the most common beacon in the response
        most_common_beacon, most_common_count = beacon_counts.most_common(1)[0]

        # If any expected beacon appears more frequently, return True (1), else False (0)
        return 1 if any(beacon_counts[beacon] >= most_common_count for beacon in expected_beacons) else 0

    except requests.exceptions.RequestException as e:
        print(f"Error fetching data for entry {entry_number}, {minutes} minutes: {e}")
        return None  # Handle errors separately

# Define the range of minutes and entry conditions
minutes_range = list(range(1, 31))
entry_conditions = {
    1: ["W10T_A7H_B-1-2"],
    6: ["W10T_A7H_B-1-19 Tränke", "W10T_A7H_B-1-7"],
    8: ["W10T_A7H_B-1-6 Tränke", "W10T_A7H_B-1-7"]
}

# Plot the binary accuracy for each entry over time
plt.figure(figsize=(10, 5))

for entry_number, expected_beacons in entry_conditions.items():
    accuracy_binary = [check_expected_beacon(entry_number, mins, expected_beacons) for mins in minutes_range]
    plt.step(minutes_range, accuracy_binary, marker='o', linestyle='-', label=f'Entry {entry_number}')

plt.xlabel("Minutes")
plt.ylabel("Correct Beacon (1=True, 0=False)")
plt.title("Expected Beacon Presence Over Time")
plt.legend()
plt.grid()
plt.ylim(-0.2, 1.2)  # To keep binary values clear on the graph
plt.show()
