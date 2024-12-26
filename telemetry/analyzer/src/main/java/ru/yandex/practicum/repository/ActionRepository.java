package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Scenario;

public interface ActionRepository extends JpaRepository<Action, Long> {

    void deleteByScenario(Scenario scenario);
}