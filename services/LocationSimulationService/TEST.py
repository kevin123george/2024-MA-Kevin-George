import requests
import time

BASE_URL = "http://localhost:9099/api/proximity/compute/all/test/csv/v2"
ENTRY_NUMBER = 1

for minutes in range(1, 94):
    url = f"{BASE_URL}?entryNumber={ENTRY_NUMBER}&minutes={minutes}"
    try:
        response = requests.get(url)
        if response.status_code == 200:
            print(f"Minutes: {minutes}, Response: {response.text}")
        else:
            print(f"Minutes: {minutes}, Error: {response.status_code}")
    except requests.exceptions.RequestException as e:
        print(f"Minutes: {minutes}, Request failed: {e}")

#     time.sleep(1)  # To avoid overwhelming the server
