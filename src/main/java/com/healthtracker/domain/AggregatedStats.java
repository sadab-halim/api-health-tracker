package com.healthtracker.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

/**
 * Aggregated statistics for 1-minute time windows.
 * Calculated from raw ApiMetric events.
 */
@Entity
@Table(name = "api_stats_1min")
public class AggregatedStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "api_name", nullable = false)
    private String apiName;

    @Column(name = "window_start", nullable = false)
    private Instant windowStart;

    @Column(name = "window_end", nullable = false)
    private Instant windowEnd;

    @Column(name = "total_requests", nullable = false)
    private Integer totalRequests;

    @Column(name = "success_count", nullable = false)
    private Integer successCount;

    @Column(name = "client_error_count", nullable = false)
    private Integer clientErrorCount;

    @Column(name = "server_error_count", nullable = false)
    private Integer serverErrorCount;

    @Column(name = "network_error_count", nullable = false)
    private Integer networkErrorCount;

    @Column(name = "avg_latency_ms", nullable = false)
    private Integer avgLatencyMs;

    @Column(name = "min_latency_ms", nullable = false)
    private Integer minLatencyMs;

    @Column(name = "max_latency_ms", nullable = false)
    private Integer maxLatencyMs;

    @Column(name = "p50_latency_ms")
    private Integer p50LatencyMs;

    @Column(name = "p95_latency_ms")
    private Integer p95LatencyMs;

    @Column(name = "p99_latency_ms")
    private Integer p99LatencyMs;

    @Column(name = "error_rate", nullable = false, precision = 10, scale = 4)
    private BigDecimal errorRate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "status_code_distribution", columnDefinition = "jsonb")
    private Map<Integer, Integer> statusCodeDistribution;

    @Column(name = "calculated_at")
    private Instant calculatedAt;

    // Constructors
    public AggregatedStats() {
        this.calculatedAt = Instant.now();
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final AggregatedStats stats = new AggregatedStats();

        public Builder apiName(String apiName) {
            stats.apiName = apiName;
            return this;
        }

        public Builder windowStart(Instant windowStart) {
            stats.windowStart = windowStart;
            return this;
        }

        public Builder windowEnd(Instant windowEnd) {
            stats.windowEnd = windowEnd;
            return this;
        }

        public Builder totalRequests(Integer totalRequests) {
            stats.totalRequests = totalRequests;
            return this;
        }

        public Builder successCount(Integer successCount) {
            stats.successCount = successCount;
            return this;
        }

        public Builder clientErrorCount(Integer clientErrorCount) {
            stats.clientErrorCount = clientErrorCount;
            return this;
        }

        public Builder serverErrorCount(Integer serverErrorCount) {
            stats.serverErrorCount = serverErrorCount;
            return this;
        }

        public Builder networkErrorCount(Integer networkErrorCount) {
            stats.networkErrorCount = networkErrorCount;
            return this;
        }

        public Builder avgLatencyMs(Integer avgLatencyMs) {
            stats.avgLatencyMs = avgLatencyMs;
            return this;
        }

        public Builder minLatencyMs(Integer minLatencyMs) {
            stats.minLatencyMs = minLatencyMs;
            return this;
        }

        public Builder maxLatencyMs(Integer maxLatencyMs) {
            stats.maxLatencyMs = maxLatencyMs;
            return this;
        }

        public Builder p50LatencyMs(Integer p50LatencyMs) {
            stats.p50LatencyMs = p50LatencyMs;
            return this;
        }

        public Builder p95LatencyMs(Integer p95LatencyMs) {
            stats.p95LatencyMs = p95LatencyMs;
            return this;
        }

        public Builder p99LatencyMs(Integer p99LatencyMs) {
            stats.p99LatencyMs = p99LatencyMs;
            return this;
        }

        public Builder errorRate(BigDecimal errorRate) {
            stats.errorRate = errorRate;
            return this;
        }

        public Builder statusCodeDistribution(Map<Integer, Integer> distribution) {
            stats.statusCodeDistribution = distribution;
            return this;
        }

        public AggregatedStats build() {
            return stats;
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getApiName() { return apiName; }
    public void setApiName(String apiName) { this.apiName = apiName; }

    public Instant getWindowStart() { return windowStart; }
    public void setWindowStart(Instant windowStart) { this.windowStart = windowStart; }

    public Instant getWindowEnd() { return windowEnd; }
    public void setWindowEnd(Instant windowEnd) { this.windowEnd = windowEnd; }

    public Integer getTotalRequests() { return totalRequests; }
    public void setTotalRequests(Integer totalRequests) { this.totalRequests = totalRequests; }

    public Integer getSuccessCount() { return successCount; }
    public void setSuccessCount(Integer successCount) { this.successCount = successCount; }

    public Integer getClientErrorCount() { return clientErrorCount; }
    public void setClientErrorCount(Integer clientErrorCount) { this.clientErrorCount = clientErrorCount; }

    public Integer getServerErrorCount() { return serverErrorCount; }
    public void setServerErrorCount(Integer serverErrorCount) { this.serverErrorCount = serverErrorCount; }

    public Integer getNetworkErrorCount() { return networkErrorCount; }
    public void setNetworkErrorCount(Integer networkErrorCount) { this.networkErrorCount = networkErrorCount; }

    public Integer getAvgLatencyMs() { return avgLatencyMs; }
    public void setAvgLatencyMs(Integer avgLatencyMs) { this.avgLatencyMs = avgLatencyMs; }

    public Integer getMinLatencyMs() { return minLatencyMs; }
    public void setMinLatencyMs(Integer minLatencyMs) { this.minLatencyMs = minLatencyMs; }

    public Integer getMaxLatencyMs() { return maxLatencyMs; }
    public void setMaxLatencyMs(Integer maxLatencyMs) { this.maxLatencyMs = maxLatencyMs; }

    public Integer getP50LatencyMs() { return p50LatencyMs; }
    public void setP50LatencyMs(Integer p50LatencyMs) { this.p50LatencyMs = p50LatencyMs; }

    public Integer getP95LatencyMs() { return p95LatencyMs; }
    public void setP95LatencyMs(Integer p95LatencyMs) { this.p95LatencyMs = p95LatencyMs; }

    public Integer getP99LatencyMs() { return p99LatencyMs; }
    public void setP99LatencyMs(Integer p99LatencyMs) { this.p99LatencyMs = p99LatencyMs; }

    public BigDecimal getErrorRate() { return errorRate; }
    public void setErrorRate(BigDecimal errorRate) { this.errorRate = errorRate; }

    public Map<Integer, Integer> getStatusCodeDistribution() { return statusCodeDistribution; }
    public void setStatusCodeDistribution(Map<Integer, Integer> distribution) {
        this.statusCodeDistribution = distribution;
    }

    public Instant getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(Instant calculatedAt) { this.calculatedAt = calculatedAt; }
}
