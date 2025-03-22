package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "product")
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {
    @Id
    @UuidGenerator
    UUID productId;

    String productName;
    String description;
    String imageSrc;

    @Enumerated(EnumType.STRING)
    QuantityState quantityState;

    @Enumerated(EnumType.STRING)
    ProductState productState;

    double rating;

    @Enumerated(EnumType.STRING)
    ProductCategory productCategory;

    double price;
}