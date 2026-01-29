-- =====================================================
-- FUNCTION: CREATE FUTURE PARTITIONS
-- =====================================================
CREATE OR REPLACE FUNCTION create_future_partitions(days_ahead INTEGER DEFAULT 7)
RETURNS TEXT AS $$
DECLARE
    partition_date DATE;
    partition_name TEXT;
    start_date TEXT;
    end_date TEXT;
    partitions_created INTEGER := 0;
BEGIN
    FOR i IN 1..days_ahead LOOP
        partition_date := CURRENT_DATE + (i || ' days')::INTERVAL;
        partition_name := 'api_metrics_' || TO_CHAR(partition_date, 'YYYY_MM_DD');
        start_date := TO_CHAR(partition_date, 'YYYY-MM-DD');
        end_date := TO_CHAR(partition_date + INTERVAL '1 day', 'YYYY-MM-DD');

        -- Check if partition already exists
        IF NOT EXISTS (
            SELECT 1 FROM pg_tables
            WHERE schemaname = 'public'
            AND tablename = partition_name
        ) THEN
            EXECUTE format(
                'CREATE TABLE IF NOT EXISTS %I PARTITION OF api_metrics FOR VALUES FROM (%L) TO (%L)',
                partition_name, start_date, end_date
            );
            partitions_created := partitions_created + 1;
            RAISE NOTICE 'Created partition: %', partition_name;
        END IF;
    END LOOP;

    RETURN 'Created ' || partitions_created || ' new partitions';
END;
$$ LANGUAGE plpgsql;

-- =====================================================
-- FUNCTION: DROP OLD PARTITIONS
-- =====================================================
CREATE OR REPLACE FUNCTION drop_old_partitions(retention_days INTEGER DEFAULT 90)
RETURNS TEXT AS $$
DECLARE
    partition_record RECORD;
    cutoff_date DATE;
    partitions_dropped INTEGER := 0;
BEGIN
    cutoff_date := CURRENT_DATE - (retention_days || ' days')::INTERVAL;

    FOR partition_record IN
        SELECT tablename
        FROM pg_tables
        WHERE schemaname = 'public'
        AND tablename LIKE 'api_metrics_%'
        AND tablename < 'api_metrics_' || TO_CHAR(cutoff_date, 'YYYY_MM_DD')
    LOOP
        EXECUTE 'DROP TABLE IF EXISTS ' || partition_record.tablename || ' CASCADE';
        partitions_dropped := partitions_dropped + 1;
        RAISE NOTICE 'Dropped partition: %', partition_record.tablename;
    END LOOP;

    RETURN 'Dropped ' || partitions_dropped || ' old partitions (older than ' || retention_days || ' days)';
END;
$$ LANGUAGE plpgsql;

-- =====================================================
-- FUNCTION: LIST ALL PARTITIONS
-- =====================================================
CREATE OR REPLACE FUNCTION list_partitions()
RETURNS TABLE(
    partition_name TEXT,
    partition_size TEXT,
    row_count BIGINT
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        c.relname::TEXT AS partition_name,
        pg_size_pretty(pg_total_relation_size(c.oid)) AS partition_size,
        c.reltuples::BIGINT AS row_count
    FROM pg_class c
    JOIN pg_namespace n ON n.oid = c.relnamespace
    WHERE c.relname LIKE 'api_metrics_%'
    AND c.relkind = 'r'
    AND n.nspname = 'public'
    ORDER BY c.relname;
END;
$$ LANGUAGE plpgsql;

-- =====================================================
-- FUNCTION: CLEANUP OLD STATS
-- =====================================================
CREATE OR REPLACE FUNCTION cleanup_old_stats(retention_days INTEGER DEFAULT 90)
RETURNS TEXT AS $$
DECLARE
    rows_deleted BIGINT;
BEGIN
    DELETE FROM api_stats_1min
    WHERE window_start < NOW() - (retention_days || ' days')::INTERVAL;

    GET DIAGNOSTICS rows_deleted = ROW_COUNT;

    RETURN 'Deleted ' || rows_deleted || ' old statistics records';
END;
$$ LANGUAGE plpgsql;

-- =====================================================
-- TEST: Create future partitions
-- =====================================================
SELECT create_future_partitions(7);

-- =====================================================
-- DISPLAY SUCCESS MESSAGE
-- =====================================================
SELECT 'Partition management functions created successfully!' AS result;
SELECT 'Functions: create_future_partitions(), drop_old_partitions(), list_partitions(), cleanup_old_stats()' AS functions;
