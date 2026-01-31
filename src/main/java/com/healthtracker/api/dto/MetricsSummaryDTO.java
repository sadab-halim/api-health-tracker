package com.healthtracker.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricsSummaryDTO {
    private Long totalRequests;
    private Long successfulRequests;
    private Long failedRequests;
    private Double successRate;
    private Double averageLatency;
    private Double p95Latency;
    private Double p99Latency;
}
