-- ================================================
-- API Health Tracker - Complete Database Schema
-- ================================================

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ================================================
-- 1. API METRICS TABLE (Raw Events)
-- ================================================
CREATE TABLE IF NOT EXISTS api_metrics (
    id BIGSERIAL PRIMARY KEY,
    event_id UUID NOT NULL,
    api_name VARCHAR(255) NOT NULL,
    endpoint VARCHAR(500) NOT NULL,
    method VARCHAR(10) NOT NULL,
    status_code INTEGER NOT NULL,
    latency_ms INTEGER NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    error_message TEXT,
    request_id VARCHAR(100),
    service_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_api_metrics_api_name ON api_metrics(api_name);
CREATE INDEX idx_api_metrics_timestamp ON api_metrics(timestamp);
CREATE INDEX idx_api_metrics_lookup ON api_metrics(api_name, timestamp);

-- ================================================
-- 2. AGGREGATED STATS TABLE (1-minute windows)
-- ================================================
CREATE TABLE IF NOT EXISTS api_stats_1min (
    id BIGSERIAL PRIMARY KEY,
    api_name VARCHAR(255) NOT NULL,
    window_start TIMESTAMP NOT NULL,
    window_end TIMESTAMP NOT NULL,
    total_requests INTEGER NOT NULL,
    success_count INTEGER NOT NULL DEFAULT 0,
    client_error_count INTEGER NOT NULL DEFAULT 0,
    server_error_count INTEGER NOT NULL DEFAULT 0,
    network_error_count INTEGER NOT NULL DEFAULT 0,
    avg_latency_ms INTEGER NOT NULL,
    min_latency_ms INTEGER NOT NULL,
    max_latency_ms INTEGER NOT NULL,
    p50_latency_ms INTEGER,
    p95_latency_ms INTEGER,
    p99_latency_ms INTEGER,
    error_rate DECIMAL(10, 4) NOT NULL,
    status_code_distribution JSONB,
    calculated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_api_stats_1min UNIQUE (api_name, window_start)
);

CREATE INDEX idx_api_stats_1min_api_name ON api_stats_1min(api_name);
CREATE INDEX idx_api_stats_1min_window_start ON api_stats_1min(window_start);
CREATE INDEX idx_api_stats_1min_lookup ON api_stats_1min(api_name, window_start);

-- ================================================
-- 3. BASELINES TABLE (7-day rolling averages)
-- ================================================
CREATE TABLE IF NOT EXISTS api_baselines (
    id BIGSERIAL PRIMARY KEY,
    api_name VARCHAR(255) NOT NULL,
    calculated_at TIMESTAMP NOT NULL,
    baseline_error_rate DECIMAL(10, 4),
    baseline_avg_latency_ms INTEGER,
    baseline_latency_stddev INTEGER,
    baseline_p95_latency_ms INTEGER,
    typical_error_codes INTEGER[],
    baseline_requests_per_min INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_api_baselines UNIQUE (api_name, calculated_at)
);

CREATE INDEX idx_api_baselines_api_name ON api_baselines(api_name);
CREATE INDEX idx_api_baselines_calculated_at ON api_baselines(calculated_at);

-- ================================================
-- 4. ANOMALIES TABLE
-- ================================================
CREATE TABLE IF NOT EXISTS anomalies (
    id BIGSERIAL PRIMARY KEY,
    api_name VARCHAR(255) NOT NULL,
    anomaly_type VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    description TEXT NOT NULL,
    detected_at TIMESTAMP NOT NULL,
    window_start TIMESTAMP NOT NULL,
    details JSONB,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    resolved_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_anomaly_type CHECK (anomaly_type IN (
        'ERROR_RATE_SPIKE', 'LATENCY_SPIKE', 'NEW_ERROR_CODE', 'VOLUME_DROP'
    )),
    CONSTRAINT chk_severity CHECK (severity IN (
        'CRITICAL', 'HIGH', 'MEDIUM', 'LOW'
    )),
    CONSTRAINT chk_status CHECK (status IN (
        'OPEN', 'RESOLVED'
    ))
);

CREATE INDEX idx_anomalies_api_name ON anomalies(api_name);
CREATE INDEX idx_anomalies_detected_at ON anomalies(detected_at);
CREATE INDEX idx_anomalies_status ON anomalies(status);
CREATE INDEX idx_anomalies_severity ON anomalies(severity);
CREATE INDEX idx_anomalies_lookup ON anomalies(api_name, detected_at, status);

-- ================================================
-- Success Message
-- ================================================
DO $$
BEGIN
    RAISE NOTICE '✓ Database initialized successfully!';
    RAISE NOTICE '✓ Tables created: api_metrics, api_stats_1min, api_baselines, anomalies';
END $$;
