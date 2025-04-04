package ru.yandex.practicum.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookedProductsDto {

    UUID bookingId;

    @NotNull
    Double deliveryWeight;

    @NotNull
    Double deliveryVolume;

    Boolean fragile;

    Map<UUID, Integer> products;

    UUID orderId;

    UUID deliveryId;
}