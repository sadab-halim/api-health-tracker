package com.healthtracker.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Raw API metric event captured from HTTP requests.
 * Stored in Kafka and then batch-written to PostgreSQL.
 */
@Entity
@Table(name = "api_metrics")
public class ApiMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "api_name", nullable = false)
    private String apiName;

    @Column(name = "endpoint", nullable = false, length = 500)
    private String endpoint;

    @Column(name = "method", nullable = false, length = 10)
    private String method;

    @Column(name = "status_code", nullable = false)
    private Integer statusCode;

    @Column(name = "latency_ms", nullable = false)
    private Integer latencyMs;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "request_id", length = 100)
    private String requestId;

    @Column(name = "service_name", length = 100)
    private String serviceName;

    @Column(name = "created_at")
    private Instant createdAt;

    // Constructors
    public ApiMetric() {
        this.eventId = UUID.randomUUID();
        this.createdAt = Instant.now();
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final ApiMetric metric = new ApiMetric();

        public Builder apiName(String apiName) {
            metric.apiName = apiName;
            return this;
        }

        public Builder endpoint(String endpoint) {
            metric.endpoint = endpoint;
            return this;
        }

        public Builder method(String method) {
            metric.method = method;
            return this;
        }

        public Builder statusCode(Integer statusCode) {
            metric.statusCode = statusCode;
            return this;
        }

        public Builder latencyMs(Integer latencyMs) {
            metric.latencyMs = latencyMs;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            metric.timestamp = timestamp;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            metric.errorMessage = errorMessage;
            return this;
        }

        public Builder requestId(String requestId) {
            metric.requestId = requestId;
            return this;
        }

        public Builder serviceName(String serviceName) {
            metric.serviceName = serviceName;
            return this;
        }

        public ApiMetric build() {
            return metric;
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UUID getEventId() { return eventId; }
    public void setEventId(UUID eventId) { this.eventId = eventId; }

    public String getApiName() { return apiName; }
    public void setApiName(String apiName) { this.apiName = apiName; }

    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public Integer getStatusCode() { return statusCode; }
    public void setStatusCode(Integer statusCode) { this.statusCode = statusCode; }

    public Integer getLatencyMs() { return latencyMs; }
    public void setLatencyMs(Integer latencyMs) { this.latencyMs = latencyMs; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
