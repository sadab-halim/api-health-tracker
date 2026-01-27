#!/bin/bash

echo "================================================"
echo "Database Verification"
echo "================================================"
echo ""

echo "Checking database tables..."
docker exec api-health-postgres psql -U postgres -d api_health -c "\dt"

echo ""
echo "Checking row counts..."
docker exec api-health-postgres psql -U postgres -d api_health << 'EOF'
SELECT 'api_metrics' as table_name, COUNT(*) as row_count FROM api_metrics
UNION ALL
SELECT 'api_stats_1min', COUNT(*) FROM api_stats_1min
UNION ALL
SELECT 'api_baselines', COUNT(*) FROM api_baselines
UNION ALL
SELECT 'anomalies', COUNT(*) FROM anomalies
ORDER BY table_name;
EOF

echo ""
echo "================================================"
echo "Verification Complete"
echo "================================================"
