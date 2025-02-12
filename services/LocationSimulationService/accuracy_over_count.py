import requests
import matplotlib.pyplot as plt
from collections import deque

BASE_URL = "http://localhost:9099/api/proximity/compute/all/test/csv/v2"
ENTRY_NUMBER = 6
EXPECTED_CLOSEST_BEACON = ["W10T_A7H_B-1-6", "W10T_A7H_B-1-7"]
WINDOW_SIZE = 5  # Adjust window size for smoothing

response_data = {}
correctness = []  # Stores 1 (correct) or 0 (incorrect) for each minute
window = deque(maxlen=WINDOW_SIZE)  # Sliding window for averaging

for minutes in range(1, 97):
    url = f"{BASE_URL}?entryNumber={ENTRY_NUMBER}&minutes={minutes}"
    try:
        response = requests.get(url)
        if response.status_code == 200:
            data = response.json()
            entry = data.get("1", {})
            closest_beacon = entry.get("Closest Beacon", "")

            # Track correctness (1 if correct, 0 otherwise)
            is_correct = 1 if closest_beacon in EXPECTED_CLOSEST_BEACON else 0
            correctness.append(is_correct)
            window.append(is_correct)

            # Calculate sliding window accuracy
            accuracy = sum(window) / len(window) * 100 if window else 0
            print(f"Minute {minutes}: Closest={closest_beacon}, Accuracy={accuracy:.1f}%")
        else:
            print(f"Error at minute {minutes}: HTTP {response.status_code}")
            correctness.append(0)  # Treat errors as incorrect
            window.append(0)
    except Exception as e:
        print(f"Request failed at minute {minutes}: {e}")
        correctness.append(0)
        window.append(0)

# Plot results
plt.figure(figsize=(12, 6))
plt.plot(range(1, 94), correctness, 'o', markersize=3, alpha=0.5, label="Raw Correctness (0/1)")
plt.plot(range(1, 94), [sum(correctness[max(0, i-WINDOW_SIZE):i])/WINDOW_SIZE*100
                        for i in range(1, 94)],
         'r-', linewidth=2, label=f"{WINDOW_SIZE}-Minute Rolling Accuracy")
plt.xlabel("Time (Minutes)")
plt.ylabel("Accuracy (%)")
plt.title("Proximity System Accuracy Over Time")
plt.ylim(-5, 105)
plt.xticks(range(0, 95, 5))
plt.yticks(range(0, 101, 10))
plt.grid(True)
plt.legend()
plt.show()