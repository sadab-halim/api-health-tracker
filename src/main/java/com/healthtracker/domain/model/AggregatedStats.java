package com.healthtracker.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Entity
@Table(name = "api_stats_1min")
public class AggregatedStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "api_name", nullable = false, length = 100)
    private String apiName;

    @NotNull
    @Column(name = "window_start", nullable = false)
    private Instant windowStart;

    @NotNull
    @Column(name = "window_end", nullable = false)
    private Instant windowEnd;

    @NotNull
    @Column(name = "total_requests", nullable = false)
    private Integer totalRequests = 0;

    @NotNull
    @Column(name = "success_count", nullable = false)
    private Integer successCount = 0;

    @NotNull
    @Column(name = "error_count", nullable = false)
    private Integer errorCount = 0;

    @NotNull
    @Column(name = "avg_latency_ms", nullable = false)
    private Integer avgLatencyMs;

    @NotNull
    @Column(name = "min_latency_ms", nullable = false)
    private Integer minLatencyMs;

    @NotNull
    @Column(name = "max_latency_ms", nullable = false)
    private Integer maxLatencyMs;

    @Column(name = "p50_latency_ms")
    private Integer p50LatencyMs;

    @Column(name = "p95_latency_ms")
    private Integer p95LatencyMs;

    @Column(name = "p99_latency_ms")
    private Integer p99LatencyMs;

    @Column(name = "error_rate", precision = 5, scale = 4)
    private BigDecimal errorRate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "status_code_distribution", columnDefinition = "jsonb")
    private Map<String, Integer> statusCodeDistribution;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // Constructors
    public AggregatedStats() {
        this.createdAt = Instant.now();
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
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

    public Instant getWindowStart() {
        return windowStart;
    }

    public void setWindowStart(Instant windowStart) {
        this.windowStart = windowStart;
    }

    public Instant getWindowEnd() {
        return windowEnd;
    }

    public void setWindowEnd(Instant windowEnd) {
        this.windowEnd = windowEnd;
    }

    public Integer getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(Integer totalRequests) {
        this.totalRequests = totalRequests;
    }

    public Integer getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public Integer getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
    }

    public Integer getAvgLatencyMs() {
        return avgLatencyMs;
    }

    public void setAvgLatencyMs(Integer avgLatencyMs) {
        this.avgLatencyMs = avgLatencyMs;
    }

    public Integer getMinLatencyMs() {
        return minLatencyMs;
    }

    public void setMinLatencyMs(Integer minLatencyMs) {
        this.minLatencyMs = minLatencyMs;
    }

    public Integer getMaxLatencyMs() {
        return maxLatencyMs;
    }

    public void setMaxLatencyMs(Integer maxLatencyMs) {
        this.maxLatencyMs = maxLatencyMs;
    }

    public Integer getP50LatencyMs() {
        return p50LatencyMs;
    }

    public void setP50LatencyMs(Integer p50LatencyMs) {
        this.p50LatencyMs = p50LatencyMs;
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

    public BigDecimal getErrorRate() {
        return errorRate;
    }

    public void setErrorRate(BigDecimal errorRate) {
        this.errorRate = errorRate;
    }

    public Map<String, Integer> getStatusCodeDistribution() {
        return statusCodeDistribution;
    }

    public void setStatusCodeDistribution(Map<String, Integer> statusCodeDistribution) {
        this.statusCodeDistribution = statusCodeDistribution;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
