package ru.yandex.practicum.handlers.hubEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;
import ru.yandex.practicum.handlers.TimestampMapper;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.kafkaClient.KafkaClient;


@Component
@RequiredArgsConstructor
@Slf4j
public class ScenarioRemovedEventHandler implements HubEventHandler {

    @Value(value = "${hubEventTopic}")
    private String topic;

    private final KafkaClient kafkaClient;


    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }

    @Override
    public void handle(HubEventProto eventProto) {
        HubEventAvro eventAvro = map(eventProto);
        ProducerRecord<String, SpecificRecordBase> producerRecord = new ProducerRecord<>(topic, null,
                eventAvro.getTimestamp().getEpochSecond(), null, eventAvro);
        kafkaClient.getProducer().send(producerRecord);
        log.info("ScenarioRemovedEvent from hub ID = {} send to topic: {}", eventAvro.getHubId(), topic);
    }

    private HubEventAvro map(HubEventProto eventProto) {
        ScenarioRemovedEventProto scenarioRemovedEventProto = eventProto.getScenarioRemoved();
        ScenarioRemovedEventAvro scenarioRemovedEventAvro = ScenarioRemovedEventAvro.newBuilder()
                .setName(scenarioRemovedEventProto.getName())
                .build();
        return HubEventAvro.newBuilder()
                .setHubId(eventProto.getHubId())
                .setTimestamp(TimestampMapper.mapToInstant(eventProto.getTimestamp()))
                .setPayload(scenarioRemovedEventAvro)
                .build();
    }
}
