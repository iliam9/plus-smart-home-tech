package ru.yandex.practicum.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.model.DimensionDto;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewProductInWarehouseRequest {

    @NotNull
    UUID productId;

    @NotNull
    Boolean fragile;

    @NotNull
    DimensionDto dimension;

    @DecimalMin(value = "1")
    double weight;
}