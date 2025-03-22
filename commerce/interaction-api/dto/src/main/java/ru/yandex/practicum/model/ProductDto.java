package ru.yandex.practicum.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDto {
    UUID productId;

    @NotNull
    @Size(min = 1)
    private String productName;

    @NotNull
    @Size(min = 1)
    private String description;

    private String imageSrc;

    @NotNull
    private QuantityState quantityState;

    @NotNull
    private ProductState productState;

    @NotNull
    private Double rating;

    private ProductCategory productCategory;

    @NotNull
    private Double price;
}