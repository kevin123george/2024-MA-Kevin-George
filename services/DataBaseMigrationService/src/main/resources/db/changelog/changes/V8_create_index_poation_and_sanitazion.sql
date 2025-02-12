-- Liquibase formatted SQL
-- changeset your_name:create_index

-- Add index to the 'latitude', 'longitude', 'beaconName', and 'deviceTime' columns
CREATE INDEX idx_positions_lat_long_beacon_time
ON position (latitude, longitude, beacon_name, device_time);


-- Add index to the 'latitude', 'longitude', 'beaconName', and 'deviceTime' columns in PositionSanitized table
CREATE INDEX idx_positionsanitized_lat_long_beacon_time
ON position_sanitized (latitude, longitude, beacon_name, device_time);
