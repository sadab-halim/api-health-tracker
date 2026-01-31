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
public class ErrorDetailsDTO {
    private String apiName;
    private String endpoint;
    private String method;
    private Integer statusCode;
    private String errorMessage;
    private Instant timestamp;
}
