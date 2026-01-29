-- =====================================================
-- ENABLE EXTENSIONS
-- =====================================================
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =====================================================
-- CREATE ENUMS
-- =====================================================
CREATE TYPE anomaly_type AS ENUM (
    'ERROR_RATE_SPIKE',
    'LATENCY_SPIKE',
    'NEW_ERROR_CODE',
    'SUDDEN_TRAFFIC_DROP',
    'TIMEOUT_INCREASE'
);

CREATE TYPE severity_level AS ENUM ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL');

-- =====================================================
-- MAIN METRICS TABLE (PARTITIONED BY DAY)
-- =====================================================
CREATE TABLE api_metrics (
    id BIGSERIAL,
    event_id UUID NOT NULL,
    api_name VARCHAR(100) NOT NULL,
    endpoint VARCHAR(500) NOT NULL,
    method VARCHAR(10) NOT NULL,
    status_code INTEGER NOT NULL,
    latency_ms INTEGER NOT NULL,
    timestamp TIMESTAMPTZ NOT NULL,
    error_message TEXT,
    metadata JSONB,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    PRIMARY KEY (id, timestamp),
    UNIQUE (api_name, endpoint, timestamp, event_id)
) PARTITION BY RANGE (timestamp);

-- =====================================================
-- CREATE INITIAL PARTITIONS (7 days from today)
-- =====================================================
-- Note: In production, these would be created dynamically
-- For now, we'll create partitions for the current week

CREATE TABLE api_metrics_2026_01_27 PARTITION OF api_metrics
    FOR VALUES FROM ('2026-01-27') TO ('2026-01-28');

CREATE TABLE api_metrics_2026_01_28 PARTITION OF api_metrics
    FOR VALUES FROM ('2026-01-28') TO ('2026-01-29');

CREATE TABLE api_metrics_2026_01_29 PARTITION OF api_metrics
    FOR VALUES FROM ('2026-01-29') TO ('2026-01-30');

CREATE TABLE api_metrics_2026_01_30 PARTITION OF api_metrics
    FOR VALUES FROM ('2026-01-30') TO ('2026-01-31');

CREATE TABLE api_metrics_2026_01_31 PARTITION OF api_metrics
    FOR VALUES FROM ('2026-01-31') TO ('2026-02-01');

CREATE TABLE api_metrics_2026_02_01 PARTITION OF api_metrics
    FOR VALUES FROM ('2026-02-01') TO ('2026-02-02');

CREATE TABLE api_metrics_2026_02_02 PARTITION OF api_metrics
    FOR VALUES FROM ('2026-02-02') TO ('2026-02-03');

CREATE TABLE api_metrics_2026_02_03 PARTITION OF api_metrics
    FOR VALUES FROM ('2026-02-03') TO ('2026-02-04');

CREATE TABLE api_metrics_2026_02_04 PARTITION OF api_metrics
    FOR VALUES FROM ('2026-02-04') TO ('2026-02-05');

CREATE TABLE api_metrics_2026_02_05 PARTITION OF api_metrics
    FOR VALUES FROM ('2026-02-05') TO ('2026-02-06');

-- =====================================================
-- INDEXES FOR QUERY PERFORMANCE
-- =====================================================
CREATE INDEX idx_api_metrics_api_time
    ON api_metrics (api_name, timestamp DESC);

CREATE INDEX idx_api_metrics_status
    ON api_metrics (api_name, status_code, timestamp DESC)
    WHERE status_code >= 400;

CREATE INDEX idx_api_metrics_endpoint
    ON api_metrics (api_name, endpoint, timestamp DESC);

-- BRIN index for timestamp (efficient for time-series data)
CREATE INDEX idx_api_metrics_timestamp_brin
    ON api_metrics USING BRIN (timestamp);

-- =====================================================
-- AGGREGATED STATISTICS (1-MINUTE WINDOWS)
-- =====================================================
CREATE TABLE api_stats_1min (
    id BIGSERIAL PRIMARY KEY,
    api_name VARCHAR(100) NOT NULL,
    window_start TIMESTAMPTZ NOT NULL,
    window_end TIMESTAMPTZ NOT NULL,
    total_requests INTEGER NOT NULL DEFAULT 0,
    success_count INTEGER NOT NULL DEFAULT 0,
    error_count INTEGER NOT NULL DEFAULT 0,
    avg_latency_ms INTEGER NOT NULL,
    min_latency_ms INTEGER NOT NULL,
    max_latency_ms INTEGER NOT NULL,
    p50_latency_ms INTEGER,
    p95_latency_ms INTEGER,
    p99_latency_ms INTEGER,
    error_rate DECIMAL(5, 4),
    status_code_distribution JSONB,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE (api_name, window_start)
);

CREATE INDEX idx_api_stats_api_window
    ON api_stats_1min (api_name, window_start DESC);

CREATE INDEX idx_api_stats_window_start
    ON api_stats_1min (window_start DESC);

-- =====================================================
-- BASELINE METRICS (7-DAY ROLLING AVERAGES)
-- =====================================================
CREATE TABLE api_baseline (
    id BIGSERIAL PRIMARY KEY,
    api_name VARCHAR(100) NOT NULL,
    calculated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    avg_error_rate DECIMAL(5, 4) NOT NULL,
    stddev_error_rate DECIMAL(5, 4) NOT NULL,
    avg_latency_ms INTEGER NOT NULL,
    stddev_latency_ms INTEGER NOT NULL,
    p95_latency_ms INTEGER NOT NULL,
    p99_latency_ms INTEGER NOT NULL,
    typical_status_codes INTEGER[] NOT NULL,
    sample_size BIGINT NOT NULL,
    UNIQUE (api_name, calculated_at)
);

CREATE INDEX idx_api_baseline_api
    ON api_baseline (api_name, calculated_at DESC);

-- =====================================================
-- ANOMALY EVENTS
-- =====================================================
CREATE TABLE anomaly_events (
    id BIGSERIAL PRIMARY KEY,
    api_name VARCHAR(100) NOT NULL,
    detected_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    anomaly_type anomaly_type NOT NULL,
    severity severity_level NOT NULL,
    baseline_value DECIMAL(10, 4),
    current_value DECIMAL(10, 4),
    threshold_multiplier DECIMAL(5, 2),
    description TEXT NOT NULL,
    sample_errors TEXT[],
    llm_summary TEXT,
    llm_processed_at TIMESTAMPTZ,
    resolved_at TIMESTAMPTZ,
    resolved_by VARCHAR(100),
    metadata JSONB,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_anomaly_events_api_detected
    ON anomaly_events (api_name, detected_at DESC);

CREATE INDEX idx_anomaly_events_unresolved
    ON anomaly_events (api_name, detected_at DESC)
    WHERE resolved_at IS NULL;

CREATE INDEX idx_anomaly_events_severity
    ON anomaly_events (severity, detected_at DESC);

-- =====================================================
-- DEAD LETTER QUEUE (FAILED EVENTS)
-- =====================================================
CREATE TABLE api_metrics_dlq (
    id BIGSERIAL PRIMARY KEY,
    original_partition INTEGER NOT NULL,
    original_offset BIGINT NOT NULL,
    failed_events JSONB NOT NULL,
    error_type VARCHAR(100) NOT NULL,
    error_message TEXT NOT NULL,
    retry_count INTEGER NOT NULL DEFAULT 0,
    last_retry_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_dlq_created
    ON api_metrics_dlq (created_at DESC);

CREATE INDEX idx_dlq_retry
    ON api_metrics_dlq (retry_count, created_at DESC)
    WHERE retry_count < 3;

-- =====================================================
-- DISPLAY SUCCESS MESSAGE
-- =====================================================
SELECT 'Main schema created successfully!' AS result;
SELECT 'Tables created: api_metrics (partitioned), api_stats_1min, api_baseline, anomaly_events, api_metrics_dlq' AS tables;
