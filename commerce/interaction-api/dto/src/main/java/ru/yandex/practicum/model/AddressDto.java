package ru.yandex.practicum.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressDto {

    @NotBlank
    String country;

    @NotBlank
    String city;

    @NotBlank
    String street;

    @NotBlank
    String house;

    @NotBlank
    String flat;

}