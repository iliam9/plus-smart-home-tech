package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.OrderDto;
import ru.yandex.practicum.request.CreateNewOrderRequest;
import ru.yandex.practicum.request.ProductReturnRequest;
import ru.yandex.practicum.service.OrderService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/order")
public class OrderController {
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
}