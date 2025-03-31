package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.model.ShoppingCartDto;

@Mapper(componentModel = "spring")
public interface ShoppingCartMapper {

    ShoppingCartDto mapToShoppingCartDto(ShoppingCart shoppingCart);
}