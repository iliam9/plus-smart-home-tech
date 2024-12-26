package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "conditions")
@SecondaryTable(name = "scenario_conditions", pkJoinColumns = @PrimaryKeyJoinColumn(name = "condition_id"))
@Getter
@Setter
@ToString
public class Condition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private ConditionType type;

    private ConditionOperation operation;

    private int value;

    @ManyToOne
    @JoinColumn(name = "scenario_id", table = "scenario_conditions")
    private Scenario scenario;

    @ManyToOne()
    @JoinColumn(name = "sensor_id", table = "scenario_conditions")
    private Sensor sensor;
}