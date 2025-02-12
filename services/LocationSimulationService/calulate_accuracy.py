import math
import requests
import json

def haversine_distance(lat1, lon1, lat2, lon2):
    """
    Calculate the great circle distance between two points on Earth using the Haversine formula.
    Returns the distance in meters.
    """
    R = 6371000.0  # Earth's radius in meters

    # Convert degrees to radians
    lat1_rad, lon1_rad = math.radians(lat1), math.radians(lon1)
    lat2_rad, lon2_rad = math.radians(lat2), math.radians(lon2)

    # Differences in coordinates
    dlat = lat2_rad - lat1_rad
    dlon = lon2_rad - lon1_rad

    # Haversine formula
    a = math.sin(dlat/2)**2 + math.cos(lat1_rad) * math.cos(lat2_rad) * math.sin(dlon/2)**2
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1-a))
    distance = R * c
    return distance

def check_estimated_accuracy(observed_lat, observed_lon, results, acceptable_radius):
    """
    For each entry in the JSON results (estimated positions), compute the distance from
    the observed coordinate to the estimated coordinates:
      - The Final coordinate (FinalLat, FinalLon)
      - The Trilaterated coordinate (TrilateratedLat, TrilateratedLon), if available.

    Then, print whether each estimated coordinate is within the acceptable radius.
    """
    print(f"Observed coordinate: ({observed_lat}, {observed_lon})")
    print(f"Acceptable radius: {acceptable_radius} meters\n")

    for key, entry in results.items():
        # Process the Final coordinate
        try:
            final_lat = float(entry.get("FinalLat"))
            final_lon = float(entry.get("FinalLon"))
        except (ValueError, TypeError):
            print(f"Entry {key}: Error processing Final coordinates")
            continue

        distance_final = haversine_distance(observed_lat, observed_lon, final_lat, final_lon)
        final_status = "within acceptable radius" if distance_final <= acceptable_radius else "outside acceptable radius"

        # Process the Trilaterated coordinate if available
        trilat_available = ("TrilateratedLat" in entry and "TrilateratedLon" in entry)
        if trilat_available:
            try:
                trilat_lat = float(entry.get("TrilateratedLat"))
                trilat_lon = float(entry.get("TrilateratedLon"))
                distance_trilat = haversine_distance(observed_lat, observed_lon, trilat_lat, trilat_lon)
                trilat_status = "within acceptable radius" if distance_trilat <= acceptable_radius else "outside acceptable radius"
            except (ValueError, TypeError):
                trilat_available = False
                trilat_status = "Error processing Trilaterated coordinates"
        else:
            trilat_status = "Not available"

        # Print results for this entry
        print(f"Entry {key}:")
        print(f"  Final coordinate:        ({final_lat}, {final_lon}) -> Distance: {distance_final:.2f} m ({final_status})")
        if trilat_available:
            print(f"  Trilaterated coordinate: ({trilat_lat}, {trilat_lon}) -> Distance: {distance_trilat:.2f} m ({trilat_status})")
        else:
            print(f"  Trilaterated coordinate: {trilat_status}")
        print()

if __name__ == "__main__":
    # Observed (true) position coordinate that you pass in
    observed_lat = 49.681612903512537
    observed_lon = 12.199201486228596

    # Define your acceptable (estimated) radius in meters (for example, 5 meters)
    acceptable_radius = 5.0

    # URL to fetch estimated positions from
    url = "http://localhost:9099/api/proximity/apiTest?entry=6&minutes=1&alpha=1.0&beta=1.0"

    try:
        # Make the API call to get estimated coordinates
        response = requests.get(url)
        response.raise_for_status()  # Raise exception for HTTP errors
        results = response.json()

        # Check each estimated coordinate against the observed coordinate
        check_estimated_accuracy(observed_lat, observed_lon, results, acceptable_radius)
    except requests.RequestException as e:
        print(f"Error fetching data from API: {e}")
    except json.JSONDecodeError as e:
        print(f"Error decoding JSON response: {e}")
