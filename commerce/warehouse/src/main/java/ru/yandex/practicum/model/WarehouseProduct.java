package ru.yandex.practicum.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@Table(name = "warehouse_product")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehouseProduct {

    @Id
    UUID productId;

    private double weight;

    private double width;

    private double height;

    private double depth;

    private boolean fragile;

    private int quantity;
}