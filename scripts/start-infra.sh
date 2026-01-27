#!/bin/bash

# macOS-compatible startup script

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo "================================================"
echo "API Health Tracker - Infrastructure Setup"
echo "================================================"
echo ""

# Check if Docker is running (macOS specific)
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}Error: Docker is not running!${NC}"
    echo "Please start Docker Desktop and try again."
    echo ""
    echo "You can start it with: open -a Docker"
    exit 1
fi

echo -e "${GREEN}✓ Docker is running${NC}"
echo ""

# Navigate to docker directory
cd "$PROJECT_ROOT/docker"

# Stop any existing containers
echo -e "${YELLOW}Stopping existing containers...${NC}"
docker-compose down -v 2>/dev/null || true

# Start services
echo -e "${YELLOW}Starting infrastructure services...${NC}"
docker-compose up -d

echo ""
echo -e "${YELLOW}Waiting for services to be ready...${NC}"

# Wait for PostgreSQL
echo -n "Waiting for PostgreSQL..."
for i in {1..30}; do
    if docker exec api-health-postgres pg_isready -U postgres > /dev/null 2>&1; then
        echo -e " ${GREEN}✓${NC}"
        break
    fi
    if [ $i -eq 30 ]; then
        echo -e " ${RED}✗ Failed${NC}"
        exit 1
    fi
    sleep 1
    echo -n "."
done

# Wait for Kafka
echo -n "Waiting for Kafka..."
for i in {1..60}; do
    if docker exec api-health-kafka kafka-broker-api-versions --bootstrap-server localhost:9092 > /dev/null 2>&1; then
        echo -e " ${GREEN}✓${NC}"
        break
    fi
    if [ $i -eq 60 ]; then
        echo -e " ${RED}✗ Failed${NC}"
        exit 1
    fi
    sleep 1
    echo -n "."
done

# Create Kafka topics
echo ""
echo -e "${YELLOW}Creating Kafka topics...${NC}"
docker exec api-health-kafka kafka-topics \
    --bootstrap-server localhost:9092 \
    --create --if-not-exists \
    --topic api-metrics \
    --partitions 3 \
    --replication-factor 1 > /dev/null 2>&1

echo -e "${GREEN}✓ Kafka topics created${NC}"

echo ""
echo "================================================"
echo -e "${GREEN}Infrastructure Started Successfully! 🎉${NC}"
echo "================================================"
echo ""
echo "Services running:"
echo "  • PostgreSQL:  localhost:5432 (postgres/postgres)"
echo "  • Kafka:       localhost:9092"
echo "  • MailHog:     http://localhost:8025"
echo ""
echo "Useful commands:"
echo "  • Check status:    docker-compose ps"
echo "  • View logs:       docker-compose logs -f"
echo "  • Stop services:   docker-compose down"
echo ""
echo "Next step: ./gradlew bootRun"
echo ""
