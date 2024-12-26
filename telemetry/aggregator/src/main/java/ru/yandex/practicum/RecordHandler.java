package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecordHandler {

    private final Map<String, SensorsSnapshotAvro> snapshots;

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        SensorsSnapshotAvro snapshot;

        if (snapshots.containsKey(event.getHubId())) {
            snapshot = snapshots.get(event.getHubId());

        if (snapshots.containsKey(event.getId())) {
            snapshot = snapshots.get(event.getId());

        } else {
            snapshot = SensorsSnapshotAvro.newBuilder()
                    .setHubId(event.getHubId())
                    .setSensorsState(new HashMap<>())
                    .setTimestamp(Instant.now())
                    .build();
        }
        Map<String, SensorStateAvro> sensorsState = snapshot.getSensorsState();

        if (isDataNotChanged(sensorsState, event)) {

        if (isDataChanged(sensorsState, event)) {

            return Optional.empty();
        }
        SensorStateAvro sensorStateAvro = SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();
        sensorsState.put(event.getId(), sensorStateAvro);
        snapshot.setSensorsState(sensorsState);
        snapshot.setTimestamp(event.getTimestamp());

        snapshots.put(snapshot.getHubId(), snapshot);


        return Optional.of(snapshot);
    }



    private boolean isDataNotChanged(Map<String, SensorStateAvro> sensorsState, SensorEventAvro event) {
        if (sensorsState != null && sensorsState.containsKey(event.getId())) {
            SensorStateAvro oldState = sensorsState.get(event.getId());
            return !isOldStateBeforeAndDateNotEquals(oldState, event);

    private boolean isDataChanged(Map<String, SensorStateAvro> sensorsState, SensorEventAvro event) {
        if (sensorsState != null && sensorsState.containsKey(event.getId())) {
            SensorStateAvro oldState = sensorsState.get(event.getId());
            return (oldState.getTimestamp().isAfter(event.getTimestamp())) ||
                    (oldState.getData() == event.getPayload());

        } else {
            return false;
        }
    }

    private boolean isOldStateBeforeAndDateNotEquals(SensorStateAvro oldState, SensorEventAvro event) {
        boolean isOldStateBefore = oldState.getTimestamp().isBefore(event.getTimestamp());
        boolean isDateEquals = oldState.getData().equals(event.getPayload());
        return  isOldStateBefore && !isDateEquals;
    }



}