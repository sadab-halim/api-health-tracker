package com.healthtracker.config;

import com.healthtracker.domain.ApiMetric;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka configuration for producing API metrics events.
 */
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, ApiMetric> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        // Connection
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // Serialization
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // Performance tuning
        configProps.put(ProducerConfig.ACKS_CONFIG, "1"); // Wait for leader acknowledgment
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384); // 16KB batches
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 10); // Wait 10ms to batch
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432); // 32MB buffer
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);

        // Timeouts
        configProps.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 5000);
        configProps.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 10000);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, ApiMetric> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
