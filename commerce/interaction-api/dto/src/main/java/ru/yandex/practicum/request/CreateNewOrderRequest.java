package ru.yandex.practicum.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.model.AddressDto;
import ru.yandex.practicum.model.ShoppingCartDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateNewOrderRequest {

    @NotNull
    String username;

    @NotNull
    ShoppingCartDto shoppingCart;

    @NotNull
    AddressDto deliveryAddress;
}