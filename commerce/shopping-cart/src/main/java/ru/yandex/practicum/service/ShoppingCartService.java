package ru.yandex.practicum.service;

import ru.yandex.practicum.model.BookedProductsDto;
import ru.yandex.practicum.request.ChangeProductQuantityRequest;
import ru.yandex.practicum.model.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ShoppingCartService {

    ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Integer> products);

    ShoppingCartDto getUsersShoppingCart(String username);

    void deactivateShoppingCart(String username);

    ShoppingCartDto removeProductsFromShoppingCart(String username, List<UUID> products);

    ShoppingCartDto changeProductQuantityInCart(String username, ChangeProductQuantityRequest request);

    BookedProductsDto bookProductFromShoppingCart(String username);
}