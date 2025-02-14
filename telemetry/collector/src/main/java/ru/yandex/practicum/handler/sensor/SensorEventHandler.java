package ru.yandex.practicum.handler.sensor;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
// Интерфейс, объединяющий все хендлеры для SensorEventProto-событий.
// Благодаря ему мы сможем внедрить все хендлеры в виде списка
// в компонент, который будет распределять получаемые события по
// их обработчикам

public interface SensorEventHandler {

        SensorEventProto.PayloadCase getMessageType();

        void handle(SensorEventProto event);
    }

