package ru.yandex.practicum.service.hubEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.kafkaClient.KafkaClient;
import ru.yandex.practicum.model.hubEvent.*;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HubEventServiceImpl implements HubEventService {

    private final KafkaClient kafkaClient;

    @Value(value = "${hubEventTopic}")
    private String topic;

    @Override
    public void collectEvent(HubEvent event) {
        HubEventAvro hubEventAvro = mapToAvro(event);
        ProducerRecord<String, SpecificRecordBase> producerRecord = new ProducerRecord<>(topic, null,
                hubEventAvro.getTimestamp().getEpochSecond(), null, hubEventAvro);
        kafkaClient.getProducer().send(producerRecord);
    }

    private HubEventAvro mapToAvro(HubEvent event) {
        Object payload;
        if (event instanceof DeviceAddedEvent deviceAddedEvent) {
            payload = DeviceAddedEventAvro.newBuilder()
                    .setId(deviceAddedEvent.getId())
                    .setType(DeviceTypeAvro.valueOf(deviceAddedEvent.getDeviceType().name()))
                    .build();
        } else if (event instanceof DeviceRemovedEvent deviceRemovedEvent) {
            payload = DeviceRemovedEventAvro.newBuilder()
                    .setId(deviceRemovedEvent.getId())
                    .build();
        } else if (event instanceof ScenarioAddedEvent scenarioAddedEvent) {
            List<DeviceActionAvro> deviceActionAvroList = scenarioAddedEvent.getActions().stream()
                    .map(this::map)
                    .toList();
            List<ScenarioConditionAvro> scenarioConditionAvroList = scenarioAddedEvent.getConditions().stream()
                    .map(this::map)
                    .toList();
            payload = ScenarioAddedEventAvro.newBuilder()
                    .setName(scenarioAddedEvent.getName())
                    .setActions(deviceActionAvroList)
                    .setConditions(scenarioConditionAvroList)
                    .build();
        } else {
            ScenarioRemovedEvent scenarioRemovedEvent = (ScenarioRemovedEvent) event;
            payload = ScenarioRemovedEventAvro.newBuilder()
                    .setName(scenarioRemovedEvent.getName())
                    .build();
        }
        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();
    }

    private DeviceActionAvro map(DeviceAction action) {
        return DeviceActionAvro.newBuilder()
                .setType(ActionTypeAvro.valueOf(action.getType().name()))
                .setSensorId(action.getSensorId())
                .setValue(action.getValue())
                .build();
    }

    private ScenarioConditionAvro map(ScenarioCondition condition) {
        return ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(ConditionTypeAvro.valueOf(condition.getType().name()))
                .setValue(condition.getValue())
                .setOperation(ConditionOperationAvro.valueOf(condition.getOperation().name()))
                .build();
    }
}
