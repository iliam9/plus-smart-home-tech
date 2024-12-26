package ru.yandex.practicum.handlers;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class SnapshotHandler {

    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;


    public void handle(SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId();
        log.info("Handle snapshot from hub ID: \"{}\"", hubId);
        List<Condition> conditions = conditionRepository.findAllByScenarioHubId(hubId);
        Supplier<Stream<SensorEventWrapper>> sensorEventStreamSupplier = () -> snapshot.getSensorsState().entrySet().stream()
                .map(e -> mapToWrapper(e.getKey(), e.getValue()));

        List<Scenario> scenarios = checkScenario(sensorEventStreamSupplier, conditions);
        if (scenarios.isEmpty()) {
            return;
        }
        log.info("Scenarios worked: {} from hub ID: \"{}\"", scenarios, hubId);
        List<Action> actions = actionRepository.findAllByScenarioIn(scenarios);
        //grpc client send
        log.info("Actions: {} send to hub ID: \"{}\"", actions, hubId);
    }

    SensorEventWrapper mapToWrapper(String sensorId, SensorStateAvro sensorStateAvro) {
        SensorEventWrapper wrapper = new SensorEventWrapper();
        wrapper.setId(sensorId);
        wrapper.setData(sensorStateAvro.getData());
        return wrapper;
    }

    private List<Scenario> checkScenario(Supplier<Stream<SensorEventWrapper>> streamSupplier, List<Condition> conditions) {
        Map<Condition, Boolean> result = new HashMap<>();
        for (Condition condition : conditions) {
            boolean isConditionDone = checkCondition(streamSupplier.get(), condition);
            result.put(condition, isConditionDone);
        }
        Map<Scenario, List<Boolean>> collect1 = result.entrySet().stream()
                .collect(Collectors.groupingBy(e -> e.getKey().getScenario(),
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())));

        List<Scenario> scenarios = collect1.entrySet().stream()
                .filter(e -> !e.getValue().contains(false))
                .map(Map.Entry::getKey)
                .toList();
        return scenarios;
    }

    private boolean checkCondition(Stream<SensorEventWrapper> stream, Condition condition) {
        return stream.filter(o -> o.getId().equals(condition.getSensor().getId()))
                .map(getFunction(condition))
                .anyMatch(getPredicate(condition));
    }

    private Predicate<Integer> getPredicate(Condition condition) {
        ConditionOperation operation = condition.getOperation();
        int value = condition.getValue();
        Predicate<Integer> predicate;
        switch (operation) {
            case EQUALS -> {
                predicate = x -> x == value;
            }
            case GREATER_THAN -> {
                predicate = x -> x > value;
            }
            case LOWER_THAN -> {
                predicate = x -> x < value;
            }
            default -> predicate = null;
        }
        return predicate;
    }

    Function<SensorEventWrapper, Integer> getFunction(Condition condition) {
        ConditionType type = condition.getType();
        Function<SensorEventWrapper, Integer> func;
        switch (type) {
            case MOTION -> {
                func = x -> {
                    MotionSensorAvro data = (MotionSensorAvro) x.getData();
                    boolean motion = data.getMotion();
                    if (motion) {
                        return 1;
                    } else {
                        return 0;
                    }
                };
            }
            case LUMINOSITY -> {
                func = x -> {
                    LightSensorAvro data = (LightSensorAvro) x.getData();
                    return data.getLuminosity();
                };
            }
            case SWITCH -> {
                func = x -> {
                    SwitchSensorAvro data = (SwitchSensorAvro) x.getData();
                    boolean state = data.getState();
                    if (state) {
                        return 1;
                    } else {
                        return 0;
                    }
                };
            }
            case TEMPERATURE -> {
                func = x -> {
                    Object object = x.getData();
                    if (object instanceof ClimateSensorAvro) {
                        ClimateSensorAvro data = (ClimateSensorAvro) object;
                        return data.getTemperatureC();
                    } else {
                        TemperatureSensorAvro data = (TemperatureSensorAvro) object;
                        return data.getTemperatureC();
                    }
                };
            }
            case CO2LEVEL -> {
                func = x -> {
                    ClimateSensorAvro data = (ClimateSensorAvro) x.getData();
                    return data.getCo2Level();
                };
            }
            case HUMIDITY -> {
                func = x -> {
                    ClimateSensorAvro data = (ClimateSensorAvro) x.getData();
                    return data.getHumidity();
                };
            }
            default -> {
                return null;
            }
        }
        return func;
    }


}