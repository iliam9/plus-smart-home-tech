package ru.yandex.practicum.service.sensorEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.kafkaClient.KafkaClient;
import ru.yandex.practicum.model.sensorEvent.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SensorEventServiceImpl implements SensorEventService {

    private final KafkaClient kafkaClient;

    @Value(value = "${sensorEventTopic}")
    private String topic;


    @Override
    public void collectEvent(SensorEvent event) {
        SensorEventAvro sensorEventAvro = mapToAvro(event);
        ProducerRecord<String, SpecificRecordBase> producerRecord = new ProducerRecord<>(topic, null,
                sensorEventAvro.getTimestamp().getEpochSecond(), null, sensorEventAvro);
        kafkaClient.getProducer().send(producerRecord);
    }

    private SensorEventAvro mapToAvro(SensorEvent event) {
        Object payload;
        if (event instanceof ClimateSensorEvent climateSensorEvent) {
            payload = ClimateSensorAvro.newBuilder()
                    .setCo2Level(climateSensorEvent.getCo2Level())
                    .setHumidity(climateSensorEvent.getHumidity())
                    .setTemperatureC(climateSensorEvent.getTemperatureC())
                    .build();
        } else if (event instanceof LightSensorEvent lightSensorEvent) {
            payload = LightSensorAvro.newBuilder()
                    .setLinkQuality(lightSensorEvent.getLinkQuality())
                    .setLuminosity(lightSensorEvent.getLuminosity())
                    .build();
        } else if (event instanceof MotionSensorEvent motionSensorEvent) {
            payload = MotionSensorAvro.newBuilder()
                    .setMotion(motionSensorEvent.isMotion())
                    .setLinkQuality(motionSensorEvent.getLinkQuality())
                    .setVoltage(motionSensorEvent.getVoltage())
                    .build();
        } else if (event instanceof SwitchSensorEvent switchSensorEvent) {
            payload = SwitchSensorAvro.newBuilder()
                    .setState(switchSensorEvent.isState())
                    .build();
        } else {
            TemperatureSensorEvent temperatureSensorEvent = (TemperatureSensorEvent) event;
            payload = TemperatureSensorAvro.newBuilder()
                    .setTemperatureC(temperatureSensorEvent.getTemperatureC())
                    .setTemperatureF(temperatureSensorEvent.getTemperatureF())
                    .build();
        }
        return SensorEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setId(event.getId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();
    }
}