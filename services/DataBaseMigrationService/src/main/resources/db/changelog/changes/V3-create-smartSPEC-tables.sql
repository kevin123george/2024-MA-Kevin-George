-- Liquibase formatted SQL

-- changeset yourname:1-create-tables

-- Create measurement table
CREATE TABLE measurement (
    id BIGSERIAL PRIMARY KEY,
    valid BOOLEAN NOT NULL,
    device VARCHAR(255) NOT NULL,
    time TIMESTAMP NOT NULL,
    geofences VARCHAR(255),
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    altitude DOUBLE PRECISION,
    beacon VARCHAR(255),
    major INT,
    minor INT,
    uuid VARCHAR(255),
    speed DOUBLE PRECISION,
    attributes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create learning_measurement table
CREATE TABLE learning_measurement (
    id BIGSERIAL PRIMARY KEY,
    wifi_ap VARCHAR(255) NOT NULL,
    cnx_time TIMESTAMP NOT NULL,
    client_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);