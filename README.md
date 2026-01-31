# 🏥 API Health Tracker - Backend

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Kafka](https://img.shields.io/badge/Apache%20Kafka-3.6.0-black.svg)](https://kafka.apache.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14+-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A high-performance, event-driven API health monitoring system built with **Spring Boot**, **Apache Kafka**, and **PostgreSQL**. Track, analyze, and visualize API metrics in real-time with powerful analytics capabilities.

---

## 📖 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [API Documentation](#api-documentation)
- [Configuration](#configuration)
- [Database Schema](#database-schema)
- [Performance](#performance)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)

---

## 🎯 Overview

The **API Health Tracker** is an enterprise-grade monitoring solution that captures, processes, and analyzes API metrics in real-time. It uses an event-driven architecture with Kafka for scalable message processing and PostgreSQL for reliable data storage.

### Key Capabilities

- 📊 **Real-time Monitoring**: Track API latency, error rates, and request volumes
- 🚀 **High Throughput**: Process 1000+ requests/second with Kafka streaming
- 📈 **Advanced Analytics**: Calculate averages, error rates, and trends
- ⚡ **Batch Processing**: Optimize database writes with intelligent batching
- 🔍 **Health Checks**: Monitor system components with Spring Boot Actuator
- 🎯 **Scalable Design**: Horizontal scaling with Kafka consumer groups

---

## 🏗️ Architecture

```
┌─────────────────┐       ┌──────────────┐       ┌─────────────────┐
│   API Clients   │──────▶│    Kafka     │──────▶│   Consumer      │
│  (Producers)    │       │    Broker    │       │   (3 threads)   │
└─────────────────┘       └──────────────┘       └─────────────────┘
                                                           │
                                                           ▼
                                                  ┌─────────────────┐
                                                  │ Batch Processor │
                                                  │  (5s window)    │
                                                  └─────────────────┘
                                                           │
                                                           ▼
                                                  ┌─────────────────┐
                                                  │   PostgreSQL    │
                                                  │    Database     │
                                                  └─────────────────┘
                                                           │
                                                           ▼
                                                  ┌─────────────────┐
                                                  │  Analytics APIs │
                                                  │  (REST Layer)   │
                                                  └─────────────────┘
```

### Data Flow

1. **Ingestion**: API metrics are sent to Kafka topic `api-metrics`
2. **Consumption**: 3 parallel consumers process messages from 6 partitions
3. **Batching**: Metrics are collected for 5 seconds or until 1000 records
4. **Persistence**: Batch insert to PostgreSQL for optimal performance
5. **Analytics**: REST APIs query aggregated data for insights

---

## ✨ Features

### Core Functionality

- ✅ **Kafka Integration**
    - Asynchronous message processing
    - 6 partitions for parallel processing
    - Consumer group with 3 concurrent threads
    - Automatic offset management

- ✅ **Database Management**
    - Optimized batch inserts (up to 1000 records)
    - Indexed queries for fast lookups
    - Connection pooling with HikariCP
    - Automatic schema creation

- ✅ **Analytics APIs**
    - Latest metrics per endpoint
    - Average latency calculations
    - Error rate analysis
    - Request count aggregation
    - Time-range queries

- ✅ **Monitoring & Health**
    - Spring Boot Actuator integration
    - Component-level health checks
    - Database connectivity monitoring
    - System metrics exposure

### Advanced Features

- 🔄 **Batch Processing**: Intelligent batching with time and size thresholds
- 🎯 **Concurrent Processing**: Multi-threaded Kafka consumers
- 📊 **Aggregation Queries**: Pre-computed analytics for fast retrieval
- 🔐 **Connection Pooling**: Efficient database resource management
- 📝 **Comprehensive Logging**: Detailed logs at all layers

---

## 🛠️ Tech Stack

| Category | Technology | Purpose |
|----------|------------|---------|
| **Language** | Java 21 | Primary programming language |
| **Framework** | Spring Boot 3.2.2 | Application framework |
| **Messaging** | Apache Kafka 3.6.0 | Event streaming platform |
| **Database** | PostgreSQL 14+ | Persistent data storage |
| **ORM** | Spring Data JPA / Hibernate | Database interaction |
| **Connection Pool** | HikariCP | Database connection management |
| **Monitoring** | Spring Boot Actuator | Health checks and metrics |
| **Build Tool** | Gradle 8.0+ | Dependency and build management |
| **Serialization** | Jackson | JSON processing |

---

## 📋 Prerequisites

### Required Software

- **Java Development Kit (JDK)**: 21 or higher
- **Apache Kafka**: 3.6.0 or higher (with Zookeeper)
- **PostgreSQL**: 14 or higher
- **Gradle**: 8.0 or higher (or use wrapper)

### Optional Tools

- **Postman** or **cURL**: API testing
- **DBeaver** or **pgAdmin**: Database management
- **IntelliJ IDEA** or **Eclipse**: IDE

### System Requirements

- **RAM**: 4GB minimum, 8GB recommended
- **Disk Space**: 2GB for dependencies and data
- **CPU**: 2+ cores recommended
- **OS**: Windows 10/11, macOS 10.15+, Linux (Ubuntu 20.04+)

---

## 🚀 Quick Start

### 1. Start PostgreSQL

```bash
# Create database
psql -U postgres
CREATE DATABASE api_health_tracker;
CREATE USER healthtracker_user WITH ENCRYPTED PASSWORD 'healthtracker123';
GRANT ALL PRIVILEGES ON DATABASE api_health_tracker TO healthtracker_user;
```

### 2. Start Kafka & Zookeeper

```bash
# Terminal 1: Start Zookeeper
cd /path/to/kafka
./bin/zookeeper-server-start.sh ./config/zookeeper.properties

# Terminal 2: Start Kafka
./bin/kafka-server-start.sh ./config/server.properties

# Terminal 3: Create Topic
./bin/kafka-topics.sh --create \
  --topic api-metrics \
  --bootstrap-server localhost:9092 \
  --partitions 6 \
  --replication-factor 1
```

### 3. Clone and Configure

```bash
git clone <repository-url>
cd api-health-tracker

# Update application.properties if needed
vim src/main/resources/application.properties
```

### 4. Build and Run

```bash
# Build
./gradlew clean build

# Run
./gradlew bootRun

# Or run JAR
java -jar build/libs/api-health-tracker-0.0.1-SNAPSHOT.jar
```

### 5. Verify Installation

```bash
# Health check
curl http://localhost:8080/actuator/health

# Send test data
curl -X POST http://localhost:8080/api/test/send-batch

# Query analytics
curl http://localhost:8080/api/analytics/latest
```

**📚 For detailed setup instructions, see [GETTING_STARTED.md](GETTING_STARTED.md)**

---

## 📡 API Documentation

### Base URL

```
http://localhost:8080
```

### Endpoints

#### 1. Health Check

**GET** `/actuator/health`

Returns the health status of all system components.

**Response:**
```json
{
  "status": "UP",
  "components": {
    "db": {"status": "UP"},
    "diskSpace": {"status": "UP"},
    "ping": {"status": "UP"}
  }
}
```

---

#### 2. Send Test Batch

**POST** `/api/test/send-batch`

Generates and sends 50 random metrics to Kafka for testing.

**Response:**
```json
{
  "message": "Successfully sent 50 metrics to Kafka",
  "recordsSent": 50,
  "topic": "api-metrics",
  "timestamp": "2026-02-01T00:37:10.535144"
}
```

---

#### 3. Get Latest Metrics

**GET** `/api/analytics/latest`

Retrieves the most recent metric for each API endpoint.

**Response:**
```json
[
  {
    "timestamp": "2026-02-01T00:37:10.535144",
    "endpoint": "/api/users",
    "method": "GET",
    "statusCode": 200,
    "latency": 120.5,
    "success": true
  }
]
```

---

#### 4. Get Average Latency

**GET** `/api/analytics/average-latency`

Calculates average response time grouped by endpoint.

**Response:**
```json
[
  {
    "endpoint": "/api/users",
    "averageLatency": 145.7,
    "requestCount": 12
  }
]
```

---

#### 5. Get Error Rate

**GET** `/api/analytics/error-rate`

Calculates error percentage for each endpoint.

**Response:**
```json
[
  {
    "endpoint": "/api/users",
    "totalRequests": 50,
    "errorCount": 5,
    "errorRate": 10.0
  }
]
```

---

#### 6. Get Request Count

**GET** `/api/analytics/request-count`

Returns total request count grouped by HTTP method.

**Response:**
```json
[
  {
    "method": "GET",
    "requestCount": 120
  },
  {
    "method": "POST",
    "requestCount": 45
  }
]
```

---

#### 7. Get Time-Range Metrics

**GET** `/api/analytics/time-range`

Query metrics within a specific time range.

**Parameters:**
- `startTime` (required): ISO 8601 format (e.g., `2026-02-01T00:00:00`)
- `endTime` (required): ISO 8601 format

**Example:**
```bash
curl "http://localhost:8080/api/analytics/time-range?startTime=2026-02-01T00:00:00&endTime=2026-02-01T01:00:00"
```

**Response:**
```json
[
  {
    "timestamp": "2026-02-01T00:37:10.535144",
    "averageLatency": 295.0,
    "requestCount": 50,
    "errorCount": 32
  }
]
```

---

## ⚙️ Configuration

### application.properties

```properties
# Server Configuration
server.port=8080

# PostgreSQL Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/api_health_tracker
spring.datasource.username=healthtracker_user
spring.datasource.password=healthtracker123

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# HikariCP Configuration
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000

# Kafka Configuration
spring.kafka.bootstrap-servers=localhost:9092

# Kafka Producer
spring.kafka.producer.acks=all
spring.kafka.producer.retries=3

# Kafka Consumer
spring.kafka.consumer.group-id=metrics-consumer-group-v2
spring.kafka.consumer.auto-offset-reset=earliest

# Kafka Listener (3 concurrent consumers)
spring.kafka.listener.concurrency=3

# Actuator
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
```

### Environment Variables (Optional)

```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=api_health_tracker
export DB_USER=healthtracker_user
export DB_PASSWORD=healthtracker123
export KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

---

## 🗄️ Database Schema

### Table: `api_metrics`

```sql
CREATE TABLE api_metrics (
    id BIGSERIAL PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL,
    endpoint VARCHAR(255) NOT NULL,
    method VARCHAR(10) NOT NULL,
    status_code INTEGER NOT NULL,
    latency DOUBLE PRECISION NOT NULL
);

-- Indexes for optimized queries
CREATE INDEX idx_endpoint ON api_metrics(endpoint);
CREATE INDEX idx_timestamp ON api_metrics(timestamp);
CREATE INDEX idx_method ON api_metrics(method);
CREATE INDEX idx_status_code ON api_metrics(status_code);
```

### Entity Mapping

```java
@Entity
@Table(name = "api_metrics")
public class ApiMetric {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;
    private String endpoint;
    private String method;
    private Integer statusCode;
    private Double latency;
}
```

---

## 🚄 Performance

### Benchmarks

| Metric | Value |
|--------|-------|
| **Throughput** | 1000-2000 messages/second |
| **API Response Time** | 50-200ms |
| **Batch Size** | Up to 1000 records |
| **Batch Interval** | 5 seconds |
| **Consumer Threads** | 3 parallel |
| **Kafka Partitions** | 6 |
| **DB Connection Pool** | 10 max, 5 min |

### Optimization Techniques

1. **Batch Processing**: Reduces database round-trips
2. **Connection Pooling**: Efficient resource management
3. **Indexed Queries**: Fast data retrieval
4. **Parallel Consumers**: Maximize Kafka throughput
5. **Asynchronous Processing**: Non-blocking operations

### Scaling Guidelines

- **Horizontal Scaling**: Add more consumer instances
- **Partition Scaling**: Increase Kafka partitions
- **Database Scaling**: Use read replicas for analytics
- **Caching**: Implement Redis for frequently accessed data

---

## 🔧 Troubleshooting

### Common Issues

#### Issue 1: Application Won't Start

**Error:** `Connection refused: localhost:5432`

**Solution:**
```bash
# Check PostgreSQL status
pg_isready -h localhost -p 5432

# Restart PostgreSQL
sudo systemctl restart postgresql
```

---

#### Issue 2: Kafka Connection Failed

**Error:** `Connection to localhost:9092 refused`

**Solution:**
```bash
# Ensure Zookeeper is running
netstat -an | grep 2181

# Ensure Kafka is running
netstat -an | grep 9092

# Restart Kafka
./bin/kafka-server-start.sh ./config/server.properties
```

---

#### Issue 3: No Data in Database

**Possible Causes:**
- Kafka topic not created
- Consumers not running
- Batch processor not triggered

**Solution:**
```bash
# Verify topic exists
./bin/kafka-topics.sh --list --bootstrap-server localhost:9092

# Check consumer group
./bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 \
  --describe --group metrics-consumer-group-v2

# Send test data
curl -X POST http://localhost:8080/api/test/send-batch
```

---

#### Issue 4: High Memory Usage

**Solution:**
```bash
# Adjust JVM heap size
java -Xms512m -Xmx2048m -jar api-health-tracker.jar

# Or in Gradle
JAVA_OPTS="-Xms512m -Xmx2048m" ./gradlew bootRun
```

---

## 📁 Project Structure

```
api-health-tracker/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/healthtracker/
│   │   │       ├── ApiHealthTrackerApplication.java
│   │   │       ├── controller/
│   │   │       │   ├── AnalyticsController.java
│   │   │       │   └── TestController.java
│   │   │       ├── model/
│   │   │       │   └── ApiMetric.java
│   │   │       ├── repository/
│   │   │       │   └── ApiMetricRepository.java
│   │   │       ├── service/
│   │   │       │   ├── KafkaProducerService.java
│   │   │       │   ├── KafkaConsumerService.java
│   │   │       │   └── BatchProcessorService.java
│   │   │       └── config/
│   │   │           └── KafkaConfig.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── logback-spring.xml
│   └── test/
│       └── java/
│           └── com/healthtracker/
│               └── ApiHealthTrackerApplicationTests.java
├── gradle/
├── build.gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
├── README.md
├── GETTING_STARTED.md
└── LICENSE
```

---

## 🧪 Testing

### Run All Tests

```bash
./gradlew test
```

### Run Specific Test

```bash
./gradlew test --tests ApiHealthTrackerApplicationTests
```

### Integration Testing

```bash
# Start all services first (PostgreSQL, Kafka, Application)

# Send test batch
curl -X POST http://localhost:8080/api/test/send-batch

# Wait 5-10 seconds for batch processing

# Verify data
curl http://localhost:8080/api/analytics/latest
```

---

## 📊 Monitoring

### Application Metrics

```bash
# View all metrics
curl http://localhost:8080/actuator/metrics

# View specific metric
curl http://localhost:8080/actuator/metrics/jvm.memory.used

# Health check
curl http://localhost:8080/actuator/health
```

### Database Monitoring

```sql
-- Check table size
SELECT pg_size_pretty(pg_total_relation_size('api_metrics'));

-- View active connections
SELECT count(*) FROM pg_stat_activity;

-- Check query performance
SELECT * FROM pg_stat_statements ORDER BY total_time DESC LIMIT 10;
```

### Kafka Monitoring

```bash
# Consumer lag
./bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 \
  --describe --group metrics-consumer-group-v2

# Topic details
./bin/kafka-topics.sh --describe --topic api-metrics \
  --bootstrap-server localhost:9092
```

---

<div align="center">

**Built with ❤️ using Spring Boot, Kafka, and PostgreSQL**

⭐ Star this repo if you find it useful!

</div>