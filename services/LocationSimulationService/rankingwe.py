import requests
import pandas as pd
from collections import defaultdict

# URL to fetch data from
url = 'http://localhost:9099/api/proximity/compute/all/test/csv/v2?entryNumber=1&minutes=6'

# Fetch the data
response = requests.get(url)
response.raise_for_status()  # Ensure we notice bad responses

# Parse JSON response
data = response.json()

# Define valid pairs
valid_pairs = {
    "W10T_A7H_B-1-2 - W10T_A7H_B-1-7",
    "W10T_A7H_B-1-2 - W10T_A7H_B-1-19 Tränke",
    "W10T_A7H_B-1-2 - W10T_A7H_B-1-1",
    "W10T_A7H_B-1-2 - W10T_A7H_B-1-3",
}

# Scoring parameters
valid_pair_score = 3
closest_beacon_score = 2
pair_frequency_score = 1

# Initialize ranking dictionary
ranking = defaultdict(int)
pair_counts = defaultdict(int)

# Compute scores
for key, entry in data.items():
    pair = entry["Optimal Pair"]
    closest = entry["Closest Beacon"]

    pair_counts[pair] += 1

    if pair in valid_pairs:
        ranking[pair] += valid_pair_score

    if closest == "W10T_A7H_B-1-2":
        ranking[pair] += closest_beacon_score

# Add frequency scores
for pair, count in pair_counts.items():
    ranking[pair] += count * pair_frequency_score

# Convert to sorted DataFrame
ranking_df = pd.DataFrame(
    ranking.items(), columns=["Beacon Pair", "Score"]
).sort_values(by="Score", ascending=False)

# Display the ranked beacon pairs
print(ranking_df)
