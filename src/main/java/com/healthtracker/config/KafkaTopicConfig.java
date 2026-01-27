package com.healthtracker.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka topic configuration.
 */
@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.topics.api-metrics.name:api-metrics}")
    private String metricsTopicName;

    @Value("${kafka.topics.api-metrics.partitions:3}")
    private int partitions;

    @Value("${kafka.topics.api-metrics.replication-factor:1}")
    private short replicationFactor;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic apiMetricsTopic() {
        return new NewTopic(metricsTopicName, partitions, replicationFactor);
    }
}
