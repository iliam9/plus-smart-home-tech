package ru.yandex.practicum.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDto {

    UUID productId;

    @NotNull
    @Size(min = 1)
    String productName;

    @NotNull
    @Size(min = 1)
    String description;

    String imageSrc;

    @NotNull
    QuantityState quantityState;

    @NotNull
    ProductState productState;

    Double rating;

    ProductCategory productCategory;

    @NotNull
    Double price;
}