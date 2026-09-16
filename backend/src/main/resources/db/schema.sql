-- Schema Definition for Call Monitoring System
CREATE TABLE IF NOT EXISTS call_monitorings (
    call_id VARCHAR(64) PRIMARY KEY,
    call_timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    cs_name VARCHAR(128) NOT NULL,
    customer_name VARCHAR(128) NOT NULL,
    sentiment_score NUMERIC(5, 2) NOT NULL
);

-- Optimization Indexes
CREATE INDEX IF NOT EXISTS idx_call_monitorings_timestamp ON call_monitorings (call_timestamp DESC);
CREATE INDEX IF NOT EXISTS idx_call_monitorings_sentiment ON call_monitorings (sentiment_score);
CREATE INDEX IF NOT EXISTS idx_call_monitorings_cs_name ON call_monitorings (cs_name);
CREATE INDEX IF NOT EXISTS idx_call_monitorings_customer_name ON call_monitorings (customer_name);
