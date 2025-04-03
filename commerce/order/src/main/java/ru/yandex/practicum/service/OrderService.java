package ru.yandex.practicum.service;

import ru.yandex.practicum.model.OrderDto;
import ru.yandex.practicum.request.CreateNewOrderRequest;
import ru.yandex.practicum.request.ProductReturnRequest;

import java.util.Collection;

public interface OrderService {

    OrderDto createOrder(CreateNewOrderRequest request);

    Collection<OrderDto> getUsersOrders(String username);

    OrderDto returnOrderProducts(ProductReturnRequest request);
}