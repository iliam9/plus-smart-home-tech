package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

import java.util.Map;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@Table(name = "orders")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {

    @Id
    @UuidGenerator
    UUID orderId;

    String username;

    @ElementCollection
    @CollectionTable(name = "order_products", joinColumns = @JoinColumn(name = "order_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    Map<UUID, Integer> products;

    UUID shoppingCartId;

    UUID paymentId;

    UUID deliveryId;

    @Enumerated(EnumType.STRING)
    OrderState state;

    Double deliveryWeight;

    Double deliveryVolume;

    Boolean fragile;

    Double totalPrice;

    Double deliveryPrice;

    Double productPrice;
}