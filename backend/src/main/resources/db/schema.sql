-- DDL Schema for Call Monitoring System (PostgreSQL / H2)

CREATE TABLE IF NOT EXISTS call_monitorings (
    call_id VARCHAR(64) NOT NULL UNIQUE,
    call_timestamp TIMESTAMP WITH TIME ZONE,
    cs_name VARCHAR(128),
    customer_name VARCHAR(128),
    sentiment_score NUMERIC(5, 2)
);
