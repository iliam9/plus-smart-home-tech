package ru.yandex.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.AnalyzerConfig;
import ru.yandex.practicum.handler.SnapshotHandler;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {
    private static final int COUNT_COMMIT_OFFSETS = 10;
    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    private final KafkaConsumer<String, SensorsSnapshotAvro> consumer;
    private final SnapshotHandler handler;
    private final AnalyzerConfig config;

    public void start() {
        try {
            consumer.subscribe(config.getSnapshotTopics());

            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer
                        .poll(config.getSnapshotConsumeAttemptTimeout());
                int count = 0;
                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    SensorsSnapshotAvro sensorsSnapshotAvro = record.value();
                    log.info("Received snapshot from hub ID = {}", sensorsSnapshotAvro.getHubId());
                    handler.handle(sensorsSnapshotAvro);
                    manageOffsets(record,count, consumer);
                    count++;
                }
                consumer.commitAsync();
            }
        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("Error:", e);
        } finally {
            try {
                consumer.commitSync(currentOffsets);
            } finally {
                consumer.close();
                log.info("Consumer closed");
            }
        }
    }

    public void stop() {
        consumer.wakeup();
    }

    private static void manageOffsets(ConsumerRecord<String, SensorsSnapshotAvro> record, int count,
                                      KafkaConsumer<String, SensorsSnapshotAvro> consumer) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if (count % COUNT_COMMIT_OFFSETS == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.warn("Error commiting offsets: {}", offsets, exception);
                }
            });
        }
    }
}