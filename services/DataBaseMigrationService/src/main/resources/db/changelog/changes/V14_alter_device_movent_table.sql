-- Create the `analysis_type` table
ALTER TABLE device_movement_pattern
    ADD COLUMN cluster_id INT NOT NULL;
