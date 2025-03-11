package ru.yandex.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.AnalyzerConfig;
import ru.yandex.practicum.handler.HubEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {

    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    private final KafkaConsumer<String, HubEventAvro> consumer;
    private final HubEventHandler handler;
    private final AnalyzerConfig config;

    @Override
    public void run() {
        try {
            consumer.subscribe(config.getHubTopics());

            while (true) {
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(config.getHubConsumeAttemptTimeout());
                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    HubEventAvro hubEventAvro = record.value();
                    log.info("Received hubEvent from hub ID = {}", hubEventAvro.getHubId());
                    handler.handle(hubEventAvro);
                    manageOffsets(record, consumer);
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

    private static void manageOffsets(ConsumerRecord<String, HubEventAvro> record,
                                      KafkaConsumer<String, HubEventAvro> consumer) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );
    }
}