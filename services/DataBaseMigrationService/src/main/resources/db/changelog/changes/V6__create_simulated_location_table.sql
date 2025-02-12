-- Liquibase formatted SQL
-- changeset your_name:create_simulated_location_table

CREATE TABLE simulated_location (
    id BIGSERIAL PRIMARY KEY,
    cattle_id VARCHAR(255) NOT NULL,
    space_id VARCHAR(255) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP WITH TIME ZONE NOT NULL
);
