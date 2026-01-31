package com.healthtracker.infrastructure.kafka.consumer;

import com.healthtracker.infrastructure.kafka.producer.ApiMetricEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class BatchAccumulator {

    private static final Logger log = LoggerFactory.getLogger(BatchAccumulator.class);
    private static final int MAX_BATCH_SIZE = 1000;
    private static final Duration MAX_WAIT_TIME = Duration.ofSeconds(5);

    private final ConcurrentHashMap<Integer, BatchBuffer> partitionBuffers = new ConcurrentHashMap<>();

    /**
     * Add event to batch buffer for a partition
     * Returns a list of events if batch is ready to flush, otherwise null
     */
    public synchronized List<ApiMetricEvent> addEvent(int partition, ApiMetricEvent event) {
        BatchBuffer buffer = partitionBuffers.computeIfAbsent(partition, k -> new BatchBuffer());

        buffer.lock.lock();
        try {
            buffer.events.add(event);

            // Check if we should flush based on size or time
            if (shouldFlush(buffer)) {
                List<ApiMetricEvent> eventsToFlush = new ArrayList<>(buffer.events);
                buffer.events.clear();
                buffer.firstEventTime = null;

                log.debug("Flushing batch for partition {}: {} events", partition, eventsToFlush.size());
                return eventsToFlush;
            }

            // Set first event time if this is the first event
            if (buffer.firstEventTime == null) {
                buffer.firstEventTime = Instant.now();
            }

            return null;
        } finally {
            buffer.lock.unlock();
        }
    }

    /**
     * Force flush all buffers (called on scheduled basis)
     */
    public List<List<ApiMetricEvent>> flushAll() {
        List<List<ApiMetricEvent>> batches = new ArrayList<>();

        for (Integer partition : partitionBuffers.keySet()) {
            BatchBuffer buffer = partitionBuffers.get(partition);

            buffer.lock.lock();
            try {
                if (!buffer.events.isEmpty() && shouldFlush(buffer)) {
                    List<ApiMetricEvent> eventsToFlush = new ArrayList<>(buffer.events);
                    buffer.events.clear();
                    buffer.firstEventTime = null;

                    log.debug("Force flushing partition {}: {} events", partition, eventsToFlush.size());
                    batches.add(eventsToFlush);
                }
            } finally {
                buffer.lock.unlock();
            }
        }

        return batches;
    }

    /**
     * Check if buffer should be flushed
     */
    private boolean shouldFlush(BatchBuffer buffer) {
        // Flush if batch size reached
        if (buffer.events.size() >= MAX_BATCH_SIZE) {
            return true;
        }

        // Flush if time threshold exceeded
        if (buffer.firstEventTime != null) {
            Duration elapsed = Duration.between(buffer.firstEventTime, Instant.now());
            if (elapsed.compareTo(MAX_WAIT_TIME) >= 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get current buffer sizes (for monitoring)
     */
    public int getTotalBufferedEvents() {
        return partitionBuffers.values().stream()
                .mapToInt(buffer -> buffer.events.size())
                .sum();
    }

    /**
     * Inner class to hold buffer state
     */
    private static class BatchBuffer {
        final List<ApiMetricEvent> events = new ArrayList<>();
        final ReentrantLock lock = new ReentrantLock();
        Instant firstEventTime = null;
    }
}
