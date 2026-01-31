package com.healthtracker.application.service;

import com.healthtracker.api.dto.*;
import com.healthtracker.domain.model.ApiMetric;
import com.healthtracker.domain.repository.ApiMetricsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetricsAnalyticsService {

    private final ApiMetricsRepository metricsRepository;

    public MetricsSummaryDTO getSummary(int hours) {
        Instant startTime = Instant.now().minus(hours, ChronoUnit.HOURS);

        Long totalRequests = metricsRepository.countByTimestampAfter(startTime);
        Long successfulRequests = metricsRepository.countSuccessfulRequests(startTime);
        Long failedRequests = totalRequests - successfulRequests;
        Double averageLatency = metricsRepository.averageLatency(startTime);

        List<Double> latencies = metricsRepository.findLatenciesOrderedDesc(startTime);
        Double p95Latency = calculatePercentile(latencies, 95);
        Double p99Latency = calculatePercentile(latencies, 99);

        Double successRate = totalRequests > 0 ? (successfulRequests * 100.0) / totalRequests : 0.0;

        return MetricsSummaryDTO.builder()
                .totalRequests(totalRequests)
                .successfulRequests(successfulRequests)
                .failedRequests(failedRequests)
                .successRate(Math.round(successRate * 100.0) / 100.0)
                .averageLatency(averageLatency != null ? Math.round(averageLatency * 100.0) / 100.0 : 0.0)
                .p95Latency(p95Latency)
                .p99Latency(p99Latency)
                .build();
    }

    public List<ApiHealthDTO> getApiHealthStats(int hours) {
        Instant startTime = Instant.now().minus(hours, ChronoUnit.HOURS);
        List<Object[]> results = metricsRepository.getApiHealthStats(startTime);

        return results.stream().map(row -> {
            String apiName = (String) row[0];
            Long totalRequests = ((Number) row[1]).longValue();
            Double avgLatency = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;
            Long successCount = ((Number) row[3]).longValue();
            Long errorCount = ((Number) row[4]).longValue();

            Double successRate = totalRequests > 0 ? (successCount * 100.0) / totalRequests : 0.0;
            String status = determineHealthStatus(successRate, avgLatency);

            return ApiHealthDTO.builder()
                    .apiName(apiName)
                    .totalRequests(totalRequests)
                    .averageLatency(Math.round(avgLatency * 100.0) / 100.0)
                    .successRate(Math.round(successRate * 100.0) / 100.0)
                    .errorCount(errorCount)
                    .status(status)
                    .build();
        }).collect(Collectors.toList());
    }

    public List<EndpointMetricsDTO> getEndpointMetrics(int hours) {
        Instant startTime = Instant.now().minus(hours, ChronoUnit.HOURS);
        List<Object[]> results = metricsRepository.getEndpointMetrics(startTime);

        return results.stream().map(row -> EndpointMetricsDTO.builder()
                .apiName((String) row[0])
                .endpoint((String) row[1])
                .method((String) row[2])
                .requestCount(((Number) row[3]).longValue())
                .averageLatency(row[4] != null ? Math.round(((Number) row[4]).doubleValue() * 100.0) / 100.0 : 0.0)
                .maxLatency(row[5] != null ? Math.round(((Number) row[5]).doubleValue() * 100.0) / 100.0 : 0.0)
                .minLatency(row[6] != null ? Math.round(((Number) row[6]).doubleValue() * 100.0) / 100.0 : 0.0)
                .build()
        ).collect(Collectors.toList());
    }

    public List<TimeSeriesDataDTO> getTimeSeriesData(int hours) {
        Instant startTime = Instant.now().minus(hours, ChronoUnit.HOURS);
        List<Object[]> results = metricsRepository.getTimeSeriesData(startTime);

        return results.stream().map(row -> {
            // Handle timestamp - could be Timestamp or java.sql.Timestamp
            LocalDateTime timestamp;
            if (row[0] instanceof Timestamp) {
                timestamp = ((Timestamp) row[0]).toLocalDateTime();
            } else if (row[0] instanceof java.sql.Timestamp) {
                timestamp = ((java.sql.Timestamp) row[0]).toLocalDateTime();
            } else {
                // Fallback
                timestamp = LocalDateTime.now();
            }

            Double avgLatency = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
            Long requestCount = row[2] != null ? ((Number) row[2]).longValue() : 0L;
            Long errorCount = row[3] != null ? ((Number) row[3]).longValue() : 0L;

            return TimeSeriesDataDTO.builder()
                    .timestamp(timestamp)
                    .averageLatency(Math.round(avgLatency * 100.0) / 100.0)
                    .requestCount(requestCount)
                    .errorCount(errorCount)
                    .build();
        }).collect(Collectors.toList());
    }

    public List<ErrorDetailsDTO> getRecentErrors(int hours, int limit) {
        Instant startTime = Instant.now().minus(hours, ChronoUnit.HOURS);
        List<ApiMetric> errors = metricsRepository.findRecentErrors(startTime);

        return errors.stream()
                .limit(limit)
                .map(metric -> ErrorDetailsDTO.builder()
                        .apiName(metric.getApiName())
                        .endpoint(metric.getEndpoint())
                        .method(metric.getMethod())
                        .statusCode(metric.getStatusCode())
                        .errorMessage(metric.getErrorMessage())
                        .timestamp(metric.getTimestamp())
                        .build()
                ).collect(Collectors.toList());
    }

    private Double calculatePercentile(List<Double> sortedValues, int percentile) {
        if (sortedValues == null || sortedValues.isEmpty()) return 0.0;
        int index = (int) Math.ceil((percentile / 100.0) * sortedValues.size()) - 1;
        index = Math.max(0, Math.min(index, sortedValues.size() - 1));
        return Math.round(sortedValues.get(index) * 100.0) / 100.0;
    }

    private String determineHealthStatus(Double successRate, Double avgLatency) {
        if (successRate >= 99.0 && avgLatency < 200) return "healthy";
        if (successRate >= 95.0 && avgLatency < 500) return "degraded";
        return "down";
    }
}
