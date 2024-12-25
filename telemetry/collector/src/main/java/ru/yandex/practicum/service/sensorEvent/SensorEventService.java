package ru.yandex.practicum.service.sensorEvent;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface SensorEventService {
    void collectEvent(SensorEventProto event);
}
