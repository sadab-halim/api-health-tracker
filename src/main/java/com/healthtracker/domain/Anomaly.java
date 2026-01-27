package com.healthtracker.domain;

import com.healthtracker.domain.enums.AnomalyStatus;
import com.healthtracker.domain.enums.AnomalyType;
import com.healthtracker.domain.enums.Severity;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;

/**
 * Detected anomaly in API behavior.
 */
@Entity
@Table(name = "anomalies")
public class Anomaly {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "api_name", nullable = false)
    private String apiName;

    @Enumerated(EnumType.STRING)
    @Column(name = "anomaly_type", nullable = false, length = 50)
    private AnomalyType anomalyType;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 20)
    private Severity severity;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "detected_at", nullable = false)
    private Instant detectedAt;

    @Column(name = "window_start", nullable = false)
    private Instant windowStart;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "details", columnDefinition = "jsonb")
    private Map<String, Object> details;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AnomalyStatus status;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @Column(name = "created_at")
    private Instant createdAt;

    // Constructors
    public Anomaly() {
        this.status = AnomalyStatus.OPEN;
        this.detectedAt = Instant.now();
        this.createdAt = Instant.now();
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Anomaly anomaly = new Anomaly();

        public Builder apiName(String apiName) {
            anomaly.apiName = apiName;
            return this;
        }

        public Builder anomalyType(AnomalyType anomalyType) {
            anomaly.anomalyType = anomalyType;
            return this;
        }

        public Builder severity(Severity severity) {
            anomaly.severity = severity;
            return this;
        }

        public Builder description(String description) {
            anomaly.description = description;
            return this;
        }

        public Builder detectedAt(Instant detectedAt) {
            anomaly.detectedAt = detectedAt;
            return this;
        }

        public Builder windowStart(Instant windowStart) {
            anomaly.windowStart = windowStart;
            return this;
        }

        public Builder details(Map<String, Object> details) {
            anomaly.details = details;
            return this;
        }

        public Builder status(AnomalyStatus status) {
            anomaly.status = status;
            return this;
        }

        public Anomaly build() {
            return anomaly;
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getApiName() { return apiName; }
    public void setApiName(String apiName) { this.apiName = apiName; }

    public AnomalyType getAnomalyType() { return anomalyType; }
    public void setAnomalyType(AnomalyType anomalyType) { this.anomalyType = anomalyType; }

    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Instant getDetectedAt() { return detectedAt; }
    public void setDetectedAt(Instant detectedAt) { this.detectedAt = detectedAt; }

    public Instant getWindowStart() { return windowStart; }
    public void setWindowStart(Instant windowStart) { this.windowStart = windowStart; }

    public Map<String, Object> getDetails() { return details; }
    public void setDetails(Map<String, Object> details) { this.details = details; }

    public AnomalyStatus getStatus() { return status; }
    public void setStatus(AnomalyStatus status) { this.status = status; }

    public Instant getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Instant resolvedAt) { this.resolvedAt = resolvedAt; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
