import requests
import matplotlib.pyplot as plt

def get_accuracy(entry_number, minutes, expected_beacons):
    url = f"http://localhost:9099/api/proximity/compute/all/test/csv/v2?entryNumber={entry_number}&minutes={minutes}"
    try:
        response = requests.get(url)
        response.raise_for_status()
        data = response.json()

        total = len(data)
        correct = sum(1 for entry in data.values() if entry["Closest Beacon"] in expected_beacons)
        accuracy = (correct / total) * 100 if total > 0 else 0

        return accuracy
    except requests.exceptions.RequestException as e:
        print(f"Error fetching data for entry {entry_number}, {minutes} minutes: {e}")
        return None

def main():
    minutes_range = range(1, 31)
    entry_conditions = {
        1: ["W10T_A7H_B-1-2"],
        6: ["W10T_A7H_B-1-19 Tränke", "W10T_A7H_B-1-7"],
        8: ["W10T_A7H_B-1-6 Tränke", "W10T_A7H_B-1-7"]
    }

    plt.figure(figsize=(10, 5))

    for entry_number, expected_beacons in entry_conditions.items():
        accuracies = [get_accuracy(entry_number, mins, expected_beacons) for mins in minutes_range]
        plt.plot(minutes_range, accuracies, marker='o', linestyle='-', label=f'Entry {entry_number}')

    plt.xlabel("Minutes")
    plt.ylabel("Accuracy (%)")
    plt.title("Accuracy of Closest Beacon over Time")
    plt.legend()
    plt.grid()
    plt.show()

if __name__ == "__main__":
    main()
