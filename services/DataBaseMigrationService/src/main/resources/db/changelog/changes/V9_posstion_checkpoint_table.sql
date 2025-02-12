CREATE TABLE processing_checkpoint (
    id SERIAL PRIMARY KEY, -- Auto-incrementing primary key
    last_processed_id BIGINT NOT NULL DEFAULT 0 -- Store the last processed ID
);


INSERT INTO processing_checkpoint (last_processed_id) VALUES (0);
