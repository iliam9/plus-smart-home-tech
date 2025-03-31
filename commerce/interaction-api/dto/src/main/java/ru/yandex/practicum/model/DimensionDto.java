package ru.yandex.practicum.model;

import jakarta.validation.constraints.DecimalMin;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DimensionDto {

    @DecimalMin(value = "1")
    double width;

    @DecimalMin(value = "1")
    double height;

    @DecimalMin(value = "1")
    double depth;
}