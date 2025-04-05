package ru.yandex.practicum.service;

import ru.yandex.practicum.model.OrderDto;
import ru.yandex.practicum.request.CreateNewOrderRequest;
import ru.yandex.practicum.request.ProductReturnRequest;

import java.util.Collection;
import java.util.UUID;

public interface OrderService {

    OrderDto createOrder(CreateNewOrderRequest request);

    Collection<OrderDto> getUsersOrders(String username);

    OrderDto returnOrderProducts(ProductReturnRequest request);

    OrderDto orderDeliverySuccessful(UUID orderId);

    OrderDto orderDeliveryFailed(UUID orderId);

    OrderDto setOrderDeliveryInProgress(UUID orderId);

    OrderDto setOrderDeliveryAssemblyFailed(UUID orderId);

    OrderDto createOrderPayment(UUID orderId);

    OrderDto setOrderPaid(UUID orderId);

    OrderDto setOrderPaymentFailed(UUID orderId);

    OrderDto calculateProductCost(UUID orderId);

    OrderDto calculateDeliveryCost(UUID orderId);

    OrderDto calculateTotalCost(UUID orderId);

    OrderDto completeOrder(UUID orderId);
}