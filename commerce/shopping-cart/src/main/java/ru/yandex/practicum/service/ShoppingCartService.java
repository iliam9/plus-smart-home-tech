package ru.yandex.practicum.service;

import ru.yandex.practicum.model.ShoppingCartDto;

import java.util.Map;
import java.util.UUID;

public interface ShoppingCartService {

    ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Integer> products);
}