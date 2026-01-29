-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Display connection info
SELECT 'Database initialized: ' || current_database() || ' at ' || NOW() AS info;

-- Health check table
CREATE TABLE IF NOT EXISTS system_health (
    id SERIAL PRIMARY KEY,
    service_name VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL,
    checked_at TIMESTAMPTZ DEFAULT NOW()
);

-- Insert initial health check record
INSERT INTO system_health (service_name, status) VALUES ('postgres', 'healthy');

-- Display success message
SELECT 'Schema initialized successfully' AS result;
