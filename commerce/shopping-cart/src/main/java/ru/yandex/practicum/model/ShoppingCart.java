package ru.yandex.practicum.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "product")
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShoppingCart {
    @Id
    @UuidGenerator
    UUID shoppingCartId;

    Map<UUID, Integer> products;

    String username;
}