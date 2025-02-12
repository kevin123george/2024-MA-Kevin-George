-- Create the analysis_config table
CREATE TABLE analysis_config (
    id BIGSERIAL PRIMARY KEY,
    key VARCHAR(255) NOT NULL UNIQUE,
    value VARCHAR(255) NOT NULL,
    data_type VARCHAR(50) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create a trigger to automatically update the `updated_at` column
CREATE OR REPLACE FUNCTION update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_updated_at
BEFORE UPDATE ON analysis_config
FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

-- Insert initial configuration values
INSERT INTO analysis_config (key, value, data_type, description, created_at, updated_at) VALUES
('executor_thread_pool_size', '8', 'int', 'Number of threads in the executor pool', NOW(), NOW()),
('analysis_time_window_weeks', '2', 'int', 'Time window (in weeks) for considering positions', NOW(), NOW()),
('location_frequency_threshold_percentage', '90', 'int', 'Threshold to identify location anomaly', NOW(), NOW()),
('top_frequent_locations_limit', '5', 'int', 'Number of top frequent locations to include in the analysis', NOW(), NOW()),
('rssi_anomaly_weak_threshold', '-90', 'double', 'RSSI value below which the signal is considered weak', NOW(), NOW()),
('rssi_anomaly_strong_threshold', '-30', 'double', 'RSSI value above which the signal is considered strong', NOW(), NOW()),
('dbscan_epsilon', '0.5', 'double', 'Epsilon value for DBSCAN clustering', NOW(), NOW()),
('dbscan_min_pts', '3', 'int', 'Minimum points to form a DBSCAN cluster', NOW(), NOW()),
('noise_point_threshold', '50', 'int', 'Percentage of noise points triggering an anomaly', NOW(), NOW()),
('min_positions_with_beacon', '1', 'int', 'Minimum positions required with beacon data', NOW(), NOW());
