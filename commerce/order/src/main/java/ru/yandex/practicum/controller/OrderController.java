package ru.yandex.practicum.controller;

import feign.FeignException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.OrderClient;
import ru.yandex.practicum.model.OrderDto;
import ru.yandex.practicum.request.CreateNewOrderRequest;
import ru.yandex.practicum.request.ProductReturnRequest;
import ru.yandex.practicum.service.OrderService;

import java.util.Collection;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/order")
public class OrderController implements OrderClient {
    private final OrderService orderService;

    @PutMapping
    public OrderDto createNewOrder(@RequestBody @Valid CreateNewOrderRequest request) {
        log.info("Received request to create new order: {}", request);
        return orderService.createOrder(request);
    }

    @GetMapping
    public Collection<OrderDto> getUsersOrders(@RequestParam String username) {
        log.info("Received request to get user {} orders", username);
        return orderService.getUsersOrders(username);
    }

    @PostMapping("/return")
    public OrderDto returnOrderProducts(@RequestBody ProductReturnRequest request) {
        log.info("Received request to return order products: {}", request);
        return orderService.returnOrderProducts(request);
    }

    @Override
    public OrderDto deliverySuccessful(UUID orderId) throws FeignException {
        log.info("Received request to set order delivery successful");
        return orderService.orderDeliverySuccessful(orderId);
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) throws FeignException {
        log.info("Received request to set delivery for order with ID:{} failed", orderId);
        return orderService.orderDeliveryFailed(orderId);
    }

    @Override
    public OrderDto orderDeliveryAssembled(UUID orderId) throws FeignException {
        log.info("Received request to set order status with ID:{} picked in delivery", orderId);
        return orderService.setOrderDeliveryInProgress(orderId);
    }
}