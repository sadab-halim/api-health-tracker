package com.healthtracker.domain.enums;

/**
 * Types of anomalies that can be detected.
 */
public enum AnomalyType {
    ERROR_RATE_SPIKE,     // Error rate exceeds baseline threshold
    LATENCY_SPIKE,        // Response time significantly higher than normal
    NEW_ERROR_CODE,       // Previously unseen HTTP error code
    VOLUME_DROP           // Request volume drops significantly
}
