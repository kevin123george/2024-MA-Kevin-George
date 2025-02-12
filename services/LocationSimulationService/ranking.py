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

def calculate_rankings(data):
    scores = defaultdict(lambda: {"closest_count": 0, "optimal_count": 0, "total_score": 0})

    for entry in data.values():
        closest_beacon = entry["Closest Beacon"]
        scores[closest_beacon]["closest_count"] += 1
        scores[closest_beacon]["total_score"] += 6

        optimal_pair = entry["Optimal Pair"].split(" - ")
        for beacon in optimal_pair:
            scores[beacon]["optimal_count"] += 1
            scores[beacon]["total_score"] += 0.5

    rankings = [
        (beacon, stats["closest_count"], stats["optimal_count"], stats["total_score"])
        for beacon, stats in scores.items()
    ]

    return sorted(rankings, key=lambda x: x[3], reverse=True)

def main():
    expected_winner = "W10T_A7H_B-1-2"
    total_iterations = 60  # Test for 60 minutes
    correct_predictions = 0
    results = []

    print("\nStarting iteration test...")
    print("Testing minute by minute for 1 hour...")

    for minute in range(1, total_iterations + 1):
        data = get_proximity_data(minutes=minute)
        if data:
            rankings = calculate_rankings(data)
            winner = rankings[0][0]
            is_correct = winner == expected_winner

            if is_correct:
                correct_predictions += 1

            accuracy = (correct_predictions / minute) * 100

            results.append([
                minute,
                winner,
                is_correct,
                f"{accuracy:.2f}%"
            ])

            # Print progress every 5 minutes
            if minute % 5 == 0:
                print(f"Processed {minute} minutes... Current accuracy: {accuracy:.2f}%")

        time.sleep(0.1)  # Small delay to avoid overwhelming the API

    # Print final results table
    print("\nDetailed Results:")
    headers = ["Minute", "Winner", "Correct?", "Running Accuracy"]
    print(tabulate(results, headers=headers, tablefmt="grid"))

    # Print final accuracy
    final_accuracy = (correct_predictions / total_iterations) * 100
    print(f"\nFinal Results:")
    print(f"Total Minutes Tested: {total_iterations}")
    print(f"Times W10T_A7H_B-1-2 Won: {correct_predictions}")
    print(f"Final Accuracy: {final_accuracy:.2f}%")

if __name__ == "__main__":
    main()