package com.healthtracker.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "api_baseline")
public class Baseline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "api_name", nullable = false, length = 100)
    private String apiName;

    @NotNull
    @Column(name = "calculated_at", nullable = false)
    private Instant calculatedAt;

    @NotNull
    @Column(name = "avg_error_rate", nullable = false, precision = 5, scale = 4)
    private BigDecimal avgErrorRate;

    @NotNull
    @Column(name = "stddev_error_rate", nullable = false, precision = 5, scale = 4)
    private BigDecimal stddevErrorRate;

    @NotNull
    @Column(name = "avg_latency_ms", nullable = false)
    private Integer avgLatencyMs;

    @NotNull
    @Column(name = "stddev_latency_ms", nullable = false)
    private Integer stddevLatencyMs;

    @NotNull
    @Column(name = "p95_latency_ms", nullable = false)
    private Integer p95LatencyMs;

    @NotNull
    @Column(name = "p99_latency_ms", nullable = false)
    private Integer p99LatencyMs;

    @NotNull
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "typical_status_codes", nullable = false, columnDefinition = "integer[]")
    private Integer[] typicalStatusCodes;

    @NotNull
    @Column(name = "sample_size", nullable = false)
    private Long sampleSize;

    // Constructors
    public Baseline() {
        this.calculatedAt = Instant.now();
    }

    @PrePersist
    protected void onCreate() {
        if (calculatedAt == null) {
            calculatedAt = Instant.now();
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public Instant getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(Instant calculatedAt) {
        this.calculatedAt = calculatedAt;
    }

    public BigDecimal getAvgErrorRate() {
        return avgErrorRate;
    }

    public void setAvgErrorRate(BigDecimal avgErrorRate) {
        this.avgErrorRate = avgErrorRate;
    }

    public BigDecimal getStddevErrorRate() {
        return stddevErrorRate;
    }

    public void setStddevErrorRate(BigDecimal stddevErrorRate) {
        this.stddevErrorRate = stddevErrorRate;
    }

    public Integer getAvgLatencyMs() {
        return avgLatencyMs;
    }

    public void setAvgLatencyMs(Integer avgLatencyMs) {
        this.avgLatencyMs = avgLatencyMs;
    }

    public Integer getStddevLatencyMs() {
        return stddevLatencyMs;
    }

    public void setStddevLatencyMs(Integer stddevLatencyMs) {
        this.stddevLatencyMs = stddevLatencyMs;
    }

    public Integer getP95LatencyMs() {
        return p95LatencyMs;
    }

    public void setP95LatencyMs(Integer p95LatencyMs) {
        this.p95LatencyMs = p95LatencyMs;
    }

    public Integer getP99LatencyMs() {
        return p99LatencyMs;
    }

    public void setP99LatencyMs(Integer p99LatencyMs) {
        this.p99LatencyMs = p99LatencyMs;
    }

    public Integer[] getTypicalStatusCodes() {
        return typicalStatusCodes;
    }

    public void setTypicalStatusCodes(Integer[] typicalStatusCodes) {
        this.typicalStatusCodes = typicalStatusCodes;
    }

    public Long getSampleSize() {
        return sampleSize;
    }

    public void setSampleSize(Long sampleSize) {
        this.sampleSize = sampleSize;
    }
}
