import requests
from collections import defaultdict
from tabulate import tabulate
import time

def get_proximity_data(entry_number=1, minutes=1):
    url = f"http://localhost:9099/api/proximity/compute/all/test/csv/v2?entryNumber={entry_number}&minutes={minutes}"
    try:
        response = requests.get(url)
        response.raise_for_status()
        return response.json()
    except requests.RequestException as e:
        print(f"Error fetching data: {e}")
        return None

def calculate_closest_beacon_rankings(data):
    # Count only Closest Beacon appearances
    beacon_counts = defaultdict(int)

    for entry in data.values():
        closest_beacon = entry["Closest Beacon"]
        beacon_counts[closest_beacon] += 1

    # Convert to list of tuples and sort by count
    rankings = sorted(beacon_counts.items(), key=lambda x: x[1], reverse=True)
    return rankings

def main():
    expected_winner = "W10T_A7H_B-1-2"
    total_iterations = 38  # Test for 60 minutes
    correct_predictions = 0
    results = []
    winner_counts = defaultdict(int)

    print("\nStarting iteration test...")
    print("Testing minute by minute for 1 hour...")
    print("Ranking based ONLY on Closest Beacon count")

    for minute in range(5, total_iterations + 5):
        data = get_proximity_data(minutes=minute)
        if data:
            rankings = calculate_closest_beacon_rankings(data)
            winner = rankings[0][0]
            winner_count = rankings[0][1]
            is_correct = winner == expected_winner

            if is_correct:
                correct_predictions += 1

            # Track how many times each beacon won
            winner_counts[winner] += 1

            accuracy = (correct_predictions / minute) * 100

            results.append([
                minute,
                winner,
                winner_count,
                is_correct,
                f"{accuracy:.2f}%"
            ])

            # Print progress every 5 minutes
            if minute % 5 == 0:
                print(f"Processed {minute} minutes... Current accuracy: {accuracy:.2f}%")

        time.sleep(0.1)  # Small delay to avoid overwhelming the API

    # Determine the beacon that won the most
    most_frequent_winner = max(winner_counts, key=winner_counts.get)
    most_frequent_wins = winner_counts[most_frequent_winner]

    # Print final results table
    print("\nDetailed Results:")
    headers = ["Minute", "Winner", "Closest Beacon Count", "Correct?", "Running Accuracy"]
    print(tabulate(results, headers=headers, tablefmt="grid"))

    # Print final accuracy and most frequent winner
    final_accuracy = (correct_predictions / total_iterations) * 100
    print(f"\nFinal Results:")
    print(f"Total Minutes Tested: {total_iterations}")
    print(f"Times W10T_A7H_B-1-2 Won: {correct_predictions}")
    print(f"Final Accuracy: {final_accuracy:.2f}%")
    print(f"Most Frequent Winner: {most_frequent_winner} (Won {most_frequent_wins} times)")



if __name__ == "__main__":
    main()
