package ru.yandex.practicum.service;

import ru.yandex.practicum.model.AddressDto;
import ru.yandex.practicum.model.BookedProductsDto;
import ru.yandex.practicum.model.ShoppingCartDto;
import ru.yandex.practicum.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.request.NewProductInWarehouseRequest;

public interface WarehouseService {

    void addNewProductToWarehouse(NewProductInWarehouseRequest request);

    void increaseProductQuantity(AddProductToWarehouseRequest request);

    AddressDto getWarehouseAddress();

    BookedProductsDto checkShoppingCart(ShoppingCartDto shoppingCart);
}