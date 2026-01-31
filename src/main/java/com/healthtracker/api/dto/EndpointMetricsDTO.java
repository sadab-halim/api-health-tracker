package com.healthtracker.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EndpointMetricsDTO {
    private String apiName;
    private String endpoint;
    private String method;
    private Long requestCount;
    private Double averageLatency;
    private Double maxLatency;
    private Double minLatency;
}
