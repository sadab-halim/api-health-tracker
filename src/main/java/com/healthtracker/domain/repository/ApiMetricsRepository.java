package com.healthtracker.domain.repository;

import com.healthtracker.domain.model.ApiMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ApiMetricsRepository extends JpaRepository<ApiMetric, Long> {

    // Overall summary
    @Query("SELECT COUNT(m) FROM ApiMetric m WHERE m.timestamp >= :startTime")
    Long countByTimestampAfter(@Param("startTime") Instant startTime);

    @Query("SELECT COUNT(m) FROM ApiMetric m WHERE m.timestamp >= :startTime AND m.statusCode >= 200 AND m.statusCode < 300")
    Long countSuccessfulRequests(@Param("startTime") Instant startTime);

    @Query("SELECT AVG(m.latencyMs) FROM ApiMetric m WHERE m.timestamp >= :startTime")
    Double averageLatency(@Param("startTime") Instant startTime);

    @Query("SELECT m.latencyMs FROM ApiMetric m WHERE m.timestamp >= :startTime ORDER BY m.latencyMs DESC")
    List<Double> findLatenciesOrderedDesc(@Param("startTime") Instant startTime);

    // By API name
    @Query("SELECT m.apiName, COUNT(m), AVG(m.latencyMs), " +
            "SUM(CASE WHEN m.statusCode >= 200 AND m.statusCode < 300 THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN m.statusCode >= 400 THEN 1 ELSE 0 END) " +
            "FROM ApiMetric m WHERE m.timestamp >= :startTime " +
            "GROUP BY m.apiName")
    List<Object[]> getApiHealthStats(@Param("startTime") Instant startTime);

    // By endpoint
    @Query("SELECT m.apiName, m.endpoint, m.method, COUNT(m), AVG(m.latencyMs), MAX(m.latencyMs), MIN(m.latencyMs) " +
            "FROM ApiMetric m WHERE m.timestamp >= :startTime " +
            "GROUP BY m.apiName, m.endpoint, m.method " +
            "ORDER BY COUNT(m) DESC")
    List<Object[]> getEndpointMetrics(@Param("startTime") Instant startTime);

    // Time series (minute-level aggregation)
    @Query(value = "SELECT " +
            "DATE_TRUNC('minute', timestamp) as time_bucket, " +
            "CAST(AVG(latency_ms) AS NUMERIC(10,2)) as avg_latency, " +
            "CAST(COUNT(*) AS BIGINT) as request_count, " +
            "CAST(SUM(CASE WHEN status_code >= 400 THEN 1 ELSE 0 END) AS BIGINT) as error_count " +
            "FROM api_metrics " +
            "WHERE timestamp >= :startTime " +
            "GROUP BY time_bucket " +
            "ORDER BY time_bucket",
            nativeQuery = true)
    List<Object[]> getTimeSeriesData(@Param("startTime") Instant startTime);

    // Recent errors
    @Query("SELECT m FROM ApiMetric m WHERE m.statusCode >= 400 AND m.timestamp >= :startTime ORDER BY m.timestamp DESC")
    List<ApiMetric> findRecentErrors(@Param("startTime") Instant startTime);
}
