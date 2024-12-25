package ru.yandex.practicum.service.hubEvent;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

public interface HubEventService {
    void collectEvent(HubEventProto event);
}
