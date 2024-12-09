package ru.yandex.practicum.service.sensorEvent;

import ru.yandex.practicum.model.sensorEvent.SensorEvent;

public interface SensorEventService {
    void collectEvent(SensorEvent event);
}
