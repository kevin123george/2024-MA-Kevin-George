-- Create the `analysis_type` table
CREATE TABLE analysis_type (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert initial data into the `analysis_type` table
INSERT INTO analysis_type (name, description, created_at, updated_at) VALUES
('executor_config', 'Configurations related to executor service', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('location_analysis', 'Analyzes device location frequency patterns', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('signal_strength_analysis', 'Analyzes signal strength for anomalies', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('movement_pattern_analysis', 'Analyzes clustering in device movement patterns', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Add `analysis_type_id` column to `analysis_config` table
ALTER TABLE analysis_config
ADD COLUMN analysis_type_id BIGINT;

-- Add foreign key constraint for `analysis_config.analysis_type_id` referencing `analysis_type.id`
ALTER TABLE analysis_config
ADD CONSTRAINT fk_analysis_type
FOREIGN KEY (analysis_type_id) REFERENCES analysis_type(id);

-- Update existing configurations to link them to their corresponding `analysis_type_id`
UPDATE analysis_config
SET analysis_type_id = (
    SELECT id FROM analysis_type WHERE name = 'executor_config'
)
WHERE key = 'executor_thread_pool_size';

UPDATE analysis_config
SET analysis_type_id = (
    SELECT id FROM analysis_type WHERE name = 'location_analysis'
)
WHERE key IN ('analysis_time_window_weeks', 'location_frequency_threshold_percentage', 'top_frequent_locations_limit');

UPDATE analysis_config
SET analysis_type_id = (
    SELECT id FROM analysis_type WHERE name = 'signal_strength_analysis'
)
WHERE key IN ('rssi_anomaly_weak_threshold', 'rssi_anomaly_strong_threshold');

UPDATE analysis_config
SET analysis_type_id = (
    SELECT id FROM analysis_type WHERE name = 'movement_pattern_analysis'
)
WHERE key IN ('dbscan_epsilon', 'dbscan_min_pts', 'noise_point_threshold');

UPDATE analysis_config
SET analysis_type_id = (
    SELECT id FROM analysis_type WHERE name = 'executor_config'
)
WHERE key = 'min_positions_with_beacon';

-- Set `analysis_type_id` as NOT NULL to enforce the relationship
ALTER TABLE analysis_config
ALTER COLUMN analysis_type_id SET NOT NULL;
