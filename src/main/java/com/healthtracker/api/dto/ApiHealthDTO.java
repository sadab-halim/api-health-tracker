package com.healthtracker.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiHealthDTO {
    private String apiName;
    private Long totalRequests;
    private Double averageLatency;
    private Double successRate;
    private Long errorCount;
    private String status; // "healthy", "degraded", "down"
}
