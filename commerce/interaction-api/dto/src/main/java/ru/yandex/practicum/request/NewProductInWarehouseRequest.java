package ru.yandex.practicum.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.model.DimensionDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewProductInWarehouseRequest {

    @NotBlank
    String productId;

    Boolean fragile;

    @NotNull
    DimensionDto dimension;

    @DecimalMin(value = "1")
    double weight;
}