package com.healthtracker.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "api_metrics")
public class ApiMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @NotBlank
    @Column(name = "api_name", nullable = false, length = 100)
    private String apiName;

    @NotBlank
    @Column(name = "endpoint", nullable = false, length = 500)
    private String endpoint;

    @NotBlank
    @Column(name = "method", nullable = false, length = 10)
    private String method;

    @NotNull
    @Column(name = "status_code", nullable = false)
    private Integer statusCode;

    @NotNull
    @Positive
    @Column(name = "latency_ms", nullable = false)
    private Integer latencyMs;

    @NotNull
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // Constructors
    public ApiMetric() {
        this.eventId = UUID.randomUUID();
        this.createdAt = Instant.now();
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (eventId == null) {
            eventId = UUID.randomUUID();
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Integer getLatencyMs() {
        return latencyMs;
    }

    public void setLatencyMs(Integer latencyMs) {
        this.latencyMs = latencyMs;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    // Builder pattern for easier object creation
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final ApiMetric metric = new ApiMetric();

        public Builder eventId(UUID eventId) {
            metric.eventId = eventId;
            return this;
        }

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

        public Builder metadata(Map<String, Object> metadata) {
            metric.metadata = metadata;
            return this;
        }

        public ApiMetric build() {
            return metric;
        }
    }
}
