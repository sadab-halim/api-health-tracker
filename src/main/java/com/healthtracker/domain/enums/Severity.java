package com.healthtracker.domain.enums;

/**
 * Severity levels for anomalies.
 */
public enum Severity {
    LOW,        // Minor issue, monitoring only
    MEDIUM,     // Moderate issue, needs attention
    HIGH,       // Serious issue, requires immediate attention
    CRITICAL    // System-critical issue, urgent response needed
}
