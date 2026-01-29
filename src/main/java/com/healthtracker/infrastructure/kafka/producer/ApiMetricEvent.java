package com.healthtracker.infrastructure.kafka.producer;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for API metrics events sent to Kafka
 */
public class ApiMetricEvent {

    @JsonProperty("event_id")
    private UUID eventId;

    @JsonProperty("api_name")
    private String apiName;

    @JsonProperty("endpoint")
    private String endpoint;

    @JsonProperty("method")
    private String method;

    @JsonProperty("status_code")
    private Integer statusCode;

    @JsonProperty("latency_ms")
    private Integer latencyMs;

    @JsonProperty("timestamp")
    private Instant timestamp;

    @JsonProperty("error_message")
    private String errorMessage;

    @JsonProperty("metadata")
    private Map<String, Object> metadata;

    // Constructors
    public ApiMetricEvent() {
        this.eventId = UUID.randomUUID();
        this.timestamp = Instant.now();
    }

    // Getters and Setters
    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Integer getLatencyMs() {
        return latencyMs;
    }

    public void setLatencyMs(Integer latencyMs) {
        this.latencyMs = latencyMs;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final ApiMetricEvent event = new ApiMetricEvent();

        public Builder eventId(UUID eventId) {
            event.eventId = eventId;
            return this;
        }

        public Builder apiName(String apiName) {
            event.apiName = apiName;
            return this;
        }

        public Builder endpoint(String endpoint) {
            event.endpoint = endpoint;
            return this;
        }

        public Builder method(String method) {
            event.method = method;
            return this;
        }

        public Builder statusCode(Integer statusCode) {
            event.statusCode = statusCode;
            return this;
        }

        public Builder latencyMs(Integer latencyMs) {
            event.latencyMs = latencyMs;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            event.timestamp = timestamp;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            event.errorMessage = errorMessage;
            return this;
        }

        public Builder metadata(Map<String, Object> metadata) {
            event.metadata = metadata;
            return this;
        }

        public ApiMetricEvent build() {
            return event;
        }
    }

    @Override
    public String toString() {
        return "ApiMetricEvent{" +
                "eventId=" + eventId +
                ", apiName='" + apiName + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", method='" + method + '\'' +
                ", statusCode=" + statusCode +
                ", latencyMs=" + latencyMs +
                ", timestamp=" + timestamp +
                '}';
    }
}
