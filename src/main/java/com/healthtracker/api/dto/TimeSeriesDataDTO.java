package com.healthtracker.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeSeriesDataDTO {
    private LocalDateTime timestamp;
    private Double averageLatency;
    private Long requestCount;
    private Long errorCount;
}
