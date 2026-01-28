package com.healthtracker.config;

import com.healthtracker.domain.ApiMetric;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka consumer configuration.
 */
@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.consumer.group-id:api-health-tracker-consumer}")
    private String groupId;

    @Bean
    public ConsumerFactory<String, ApiMetric> consumerFactory() {
        Map<String, Object> props = new HashMap<>();

        // Connection
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        // Deserialization
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.healthtracker.domain");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, ApiMetric.class.getName());

        // Consumer behavior
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // Manual commit for batch
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1000); // Batch size
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000); // 5 minutes

        // Performance
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1024); // 1KB
        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500); // Wait 500ms for batch

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new JsonDeserializer<>(ApiMetric.class, false)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ApiMetric> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ApiMetric> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(3); // 3 consumer threads (matches 3 partitions)
        factory.setBatchListener(true); // Enable batch processing
        factory.getContainerProperties().setAckMode(
                org.springframework.kafka.listener.ContainerProperties.AckMode.MANUAL
        );

        return factory;
    }
}
