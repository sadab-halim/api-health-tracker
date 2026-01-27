package com.healthtracker.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Statistical baseline calculated from 7 days of historical data.
 * Used as reference for anomaly detection.
 */
@Entity
@Table(name = "api_baselines")
public class Baseline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "api_name", nullable = false)
    private String apiName;

    @Column(name = "calculated_at", nullable = false)
    private Instant calculatedAt;

    @Column(name = "baseline_error_rate", precision = 10, scale = 4)
    private BigDecimal baselineErrorRate;

    @Column(name = "baseline_avg_latency_ms")
    private Integer baselineAvgLatencyMs;

    @Column(name = "baseline_latency_stddev")
    private Integer baselineLatencyStddev;

    @Column(name = "baseline_p95_latency_ms")
    private Integer baselineP95LatencyMs;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "typical_error_codes", columnDefinition = "integer[]")
    private List<Integer> typicalErrorCodes;

    @Column(name = "baseline_requests_per_min")
    private Integer baselineRequestsPerMin;

    @Column(name = "created_at")
    private Instant createdAt;

    // Constructors
    public Baseline() {
        this.createdAt = Instant.now();
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Baseline baseline = new Baseline();

        public Builder apiName(String apiName) {
            baseline.apiName = apiName;
            return this;
        }

        public Builder calculatedAt(Instant calculatedAt) {
            baseline.calculatedAt = calculatedAt;
            return this;
        }

        public Builder baselineErrorRate(BigDecimal baselineErrorRate) {
            baseline.baselineErrorRate = baselineErrorRate;
            return this;
        }

        public Builder baselineAvgLatencyMs(Integer baselineAvgLatencyMs) {
            baseline.baselineAvgLatencyMs = baselineAvgLatencyMs;
            return this;
        }

        public Builder baselineLatencyStddev(Integer baselineLatencyStddev) {
            baseline.baselineLatencyStddev = baselineLatencyStddev;
            return this;
        }

        public Builder baselineP95LatencyMs(Integer baselineP95LatencyMs) {
            baseline.baselineP95LatencyMs = baselineP95LatencyMs;
            return this;
        }

        public Builder typicalErrorCodes(List<Integer> typicalErrorCodes) {
            baseline.typicalErrorCodes = typicalErrorCodes;
            return this;
        }

        public Builder baselineRequestsPerMin(Integer baselineRequestsPerMin) {
            baseline.baselineRequestsPerMin = baselineRequestsPerMin;
            return this;
        }

        public Baseline build() {
            return baseline;
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getApiName() { return apiName; }
    public void setApiName(String apiName) { this.apiName = apiName; }

    public Instant getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(Instant calculatedAt) { this.calculatedAt = calculatedAt; }

    public BigDecimal getBaselineErrorRate() { return baselineErrorRate; }
    public void setBaselineErrorRate(BigDecimal baselineErrorRate) {
        this.baselineErrorRate = baselineErrorRate;
    }

    public Integer getBaselineAvgLatencyMs() { return baselineAvgLatencyMs; }
    public void setBaselineAvgLatencyMs(Integer baselineAvgLatencyMs) {
        this.baselineAvgLatencyMs = baselineAvgLatencyMs;
    }

    public Integer getBaselineLatencyStddev() { return baselineLatencyStddev; }
    public void setBaselineLatencyStddev(Integer baselineLatencyStddev) {
        this.baselineLatencyStddev = baselineLatencyStddev;
    }

    public Integer getBaselineP95LatencyMs() { return baselineP95LatencyMs; }
    public void setBaselineP95LatencyMs(Integer baselineP95LatencyMs) {
        this.baselineP95LatencyMs = baselineP95LatencyMs;
    }

    public List<Integer> getTypicalErrorCodes() { return typicalErrorCodes; }
    public void setTypicalErrorCodes(List<Integer> typicalErrorCodes) {
        this.typicalErrorCodes = typicalErrorCodes;
    }

    public Integer getBaselineRequestsPerMin() { return baselineRequestsPerMin; }
    public void setBaselineRequestsPerMin(Integer baselineRequestsPerMin) {
        this.baselineRequestsPerMin = baselineRequestsPerMin;
    }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
