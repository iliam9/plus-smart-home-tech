package ru.yandex.practicum.model;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDto {

    @NotNull
    UUID orderId;

    @NotNull
    String username;

    @NotNull
    UUID shoppingCartId;

    @NotNull
    Map<UUID, Integer> products;

    UUID paymentId;

    UUID deliveryId;

    @NotNull
    OrderState state;

    Double deliveryWeight;

    Double deliveryVolume;

    Boolean fragile;

    Double totalPrice;

    Double deliveryPrice;

    Double productPrice;
}