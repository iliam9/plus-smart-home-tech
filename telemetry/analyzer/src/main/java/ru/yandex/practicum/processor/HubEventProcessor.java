package ru.yandex.practicum.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.VoidDeserializer;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.handlers.HubEventHandler;
import ru.yandex.practicum.kafka.deserializer.HubEventDeserializer;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Component
@Slf4j
public class HubEventProcessor implements Runnable {

    private static final List<String> TOPICS = List.of("telemetry.hubs.v1");
    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);

    private final KafkaConsumer<String, HubEventAvro> consumer;
    private final HubEventHandler handler;

    public HubEventProcessor(HubEventHandler handler) {
        consumer = new KafkaConsumer<>(getConsumerProperties());
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            consumer.subscribe(TOPICS);

            while (true) {
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);
                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    HubEventAvro hubEventAvro = record.value();
                    log.info("Received hubEvent from hub ID = {}", hubEventAvro.getHubId());
                    handler.handle(hubEventAvro);
                    manageOffsets(record, consumer);
                }
            }
        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("Error:", e);
        } finally {
            try {
                consumer.commitSync(currentOffsets);
            } finally {
                consumer.close();
                log.info("Consumer close");
            }
        }
    }

    private static Properties getConsumerProperties() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, "consumer");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "analyzing");
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, VoidDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, HubEventDeserializer.class);
        return properties;
    }

    private static void manageOffsets(ConsumerRecord<String, HubEventAvro> record,
                                      KafkaConsumer<String, HubEventAvro> consumer) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );
    }

}