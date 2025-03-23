package ru.yandex.practicum.service;

import ru.yandex.practicum.request.NewProductInWarehouseRequest;

public interface WarehouseService {

    void addNewProductToWarehouse(NewProductInWarehouseRequest request);
}