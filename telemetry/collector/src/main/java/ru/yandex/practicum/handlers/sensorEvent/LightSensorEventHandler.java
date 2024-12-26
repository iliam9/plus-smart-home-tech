package ru.yandex.practicum.handlers.sensorEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.handlers.TimestampMapper;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafkaClient.KafkaClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class LightSensorEventHandler implements SensorEventHandler {

    @Value(value = "${sensorEventTopic}")
    private String topic;

    private final KafkaClient kafkaClient;


    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto eventProto) {
        SensorEventAvro eventAvro = map(eventProto);
        ProducerRecord<String, SpecificRecordBase> producerRecord = new ProducerRecord<>(topic, null,
                eventAvro.getTimestamp().getEpochSecond(), null, eventAvro);
        kafkaClient.getProducer().send(producerRecord);
        log.info("Event from sensor ID = {} send to topic: {}", eventAvro.getId(), topic);
    }

    private SensorEventAvro map(SensorEventProto eventProto) {
        LightSensorProto lightSensorProto = eventProto.getLightSensorEvent();
        LightSensorAvro lightSensorAvro = LightSensorAvro.newBuilder()
                .setLuminosity(lightSensorProto.getLuminosity())
                .setLinkQuality(lightSensorProto.getLinkQuality())
                .build();
        return SensorEventAvro.newBuilder()
                .setId(eventProto.getId())
                .setHubId(eventProto.getHubId())
                .setTimestamp(TimestampMapper.mapToInstant(eventProto.getTimestamp()))
                .setPayload(lightSensorAvro)
                .build();
    }
}
