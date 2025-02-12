import requests
import math

def haversine(lat1, lon1, lat2, lon2):
    """
    Compute the great-circle distance between two points on Earth (specified in decimal degrees)
    using the haversine formula.
    Returns the distance in meters.
    """
    R = 6371000  # Earth's radius in meters
    phi1 = math.radians(lat1)
    phi2 = math.radians(lat2)
    delta_phi = math.radians(lat2 - lat1)
    delta_lambda = math.radians(lon2 - lon1)
    a = math.sin(delta_phi/2)**2 + math.cos(phi1)*math.cos(phi2)*math.sin(delta_lambda/2)**2
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    return R * c

# Observed location (ground truth)
observed_lat = 49.681626877419781
observed_lon = 12.199274534123294

# Define parameter ranges.
# Here minutes from 1 to 10, and alpha and beta from 0.1 to 0.9.
minutes_range = range(1, 11)
alpha_values = [round(x * 0.1, 1) for x in range(1, 10)]
beta_values = [round(x * 0.1, 1) for x in range(1, 10)]

target_beacon = "W10T_A7H_B-1-2"  # The beacon we want to count

# To store results for each parameter combination.
results = []

# Best combination based on target beacon counts (combined from two fields).
best_combined_count = -1
best_params_combined = {}

# Best combination based on spatial accuracy (lowest average error).
best_accuracy_value = float('inf')
best_accuracy_params = {}

# Loop over all combinations of minutes, alpha, and beta.
for minutes in minutes_range:
    for alpha in alpha_values:
        for beta in beta_values:
            # Construct the API URL with the current parameters.
            url = (f"http://localhost:9099/api/proximity/apiTest?"
                   f"entry=1&minutes={minutes}&alpha={alpha}&beta={beta}")
            try:
                response = requests.get(url)
                response.raise_for_status()  # Raise an error if the HTTP request failed.
                data = response.json()
            except Exception as e:
                print(f"Error fetching data for minutes={minutes}, alpha={alpha}, beta={beta}: {e}")
                continue

            total_entries = len(data)
            if total_entries == 0:
                continue  # Skip if no data is returned

            # ----- Target Beacon Counting -----
            # Count occurrences in the "ClosestBeacon" field.
            closest_count = sum(
                1 for entry in data.values()
                if entry.get("ClosestBeacon") == target_beacon
            )
            closest_percentage = (closest_count / total_entries * 100)

            # Count occurrences in the "OptimalPair" field.
            optimal_count = 0
            for entry in data.values():
                optimal_pair = entry.get("OptimalPair", "")
                if optimal_pair:
                    # Split the string on " - " and trim spaces.
                    parts = [part.strip() for part in optimal_pair.split(" - ")]
                    if target_beacon in parts:
                        optimal_count += 1
            optimal_percentage = (optimal_count / total_entries * 100)

            # Combined count (an entry may count in both fields)
            combined_count = closest_count + optimal_count
            # There are two opportunities per entry so combined percentage is calculated accordingly.
            combined_percentage = (combined_count / (total_entries * 2) * 100)

            # ----- Spatial Accuracy Calculation -----
            distance_errors = []
            within_2_meter_count = 0
            for entry in data.values():
                try:
                    # Use the "FinalLat" and "FinalLon" as the predicted coordinates.
                    predicted_lat = float(entry.get("FinalLat", 0))
                    predicted_lon = float(entry.get("FinalLon", 0))
                except (ValueError, TypeError):
                    continue
                error = haversine(observed_lat, observed_lon, predicted_lat, predicted_lon)
                distance_errors.append(error)
                if error <= 2:  # within 2 meters is considered "okay"
                    within_2_meter_count += 1

            avg_error = sum(distance_errors) / total_entries if total_entries > 0 else None
            within_2_percentage = (within_2_meter_count / total_entries * 100)

            # Store the result for this parameter combination.
            result = {
                'minutes': minutes,
                'alpha': alpha,
                'beta': beta,
                'closest_count': closest_count,
                'closest_percentage': closest_percentage,
                'optimal_count': optimal_count,
                'optimal_percentage': optimal_percentage,
                'combined_count': combined_count,
                'combined_percentage': combined_percentage,
                'avg_error_m': avg_error,
                'within_2m_count': within_2_meter_count,
                'within_2m_percentage': within_2_percentage
            }
            results.append(result)

            print(f"Parameters: minutes={minutes}, alpha={alpha}, beta={beta}")
            print(f"  -> Beacon Counts: Closest: {closest_count} ({closest_percentage:.2f}%), "
                  f"Optimal: {optimal_count} ({optimal_percentage:.2f}%), "
                  f"Combined: {combined_count} ({combined_percentage:.2f}%)")
            print(f"  -> Spatial Accuracy: Average Error: {avg_error:.2f} m, "
                  f"Within 2m: {within_2_meter_count}/{total_entries} ({within_2_percentage:.2f}%)")
            print("-" * 80)

            # Update the best combination based on combined beacon counts.
            if combined_count > best_combined_count:
                best_combined_count = combined_count
                best_params_combined = result

            # Update the best combination based on spatial accuracy (lowest avg error).
            if avg_error is not None and avg_error < best_accuracy_value:
                best_accuracy_value = avg_error
                best_accuracy_params = result

# Print the best parameter combination based on combined beacon counts.
print("\nBest parameter combination based on target beacon counts:")
print(f"Minutes: {best_params_combined.get('minutes')}, "
      f"Alpha: {best_params_combined.get('alpha')}, "
      f"Beta: {best_params_combined.get('beta')}")
print(f"ClosestBeacon count: {best_params_combined.get('closest_count')} "
      f"({best_params_combined.get('closest_percentage'):.2f}%)")
print(f"OptimalPair count: {best_params_combined.get('optimal_count')} "
      f"({best_params_combined.get('optimal_percentage'):.2f}%)")
print(f"Combined count: {best_params_combined.get('combined_count')} "
      f"({best_params_combined.get('combined_percentage'):.2f}%)")
print(f"Average spatial error: {best_params_combined.get('avg_error_m'):.2f} m, "
      f"Within 2m: {best_params_combined.get('within_2m_count')} "
      f"({best_params_combined.get('within_2m_percentage'):.2f}%)")

# Print the best parameter combination based on spatial accuracy.
print("\nBest parameter combination based on spatial accuracy (lowest average error):")
print(f"Minutes: {best_accuracy_params.get('minutes')}, "
      f"Alpha: {best_accuracy_params.get('alpha')}, "
      f"Beta: {best_accuracy_params.get('beta')}")
print(f"Average spatial error: {best_accuracy_params.get('avg_error_m'):.2f} m, "
      f"Within 2m: {best_accuracy_params.get('within_2m_count')} "
      f"({best_accuracy_params.get('within_2m_percentage'):.2f}%)")
print(f"Combined beacon count: {best_accuracy_params.get('combined_count')} "
      f"({best_accuracy_params.get('combined_percentage'):.2f}%)")
