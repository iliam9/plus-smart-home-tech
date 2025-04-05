package ru.yandex.practicum.controller;

import feign.FeignException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    public OrderDto returnOrderProducts(@RequestBody @Valid ProductReturnRequest request) {
        log.info("Received request to return order products: {}", request);
        return orderService.returnOrderProducts(request);
    }

    @Override
    public OrderDto deliverySuccessful(UUID orderId) throws FeignException {
        log.info("Received request to set order delivery successful");
        return orderService.setOrderDeliverySuccessful(orderId);
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) throws FeignException {
        log.info("Received request to set delivery for order with ID:{} failed", orderId);
        return orderService.setOrderDeliveryFailed(orderId);
    }

    @Override
    public OrderDto assemblySuccessful(UUID orderId) throws FeignException {
        log.info("Received request to set order status with ID:{} picked in delivery", orderId);
        return orderService.setOrderDeliveryInProgress(orderId);
    }

    @Override
    public OrderDto assemblyFailed(UUID orderId) throws FeignException {
        log.info("Received request to set order status with ID:{} assembly failed", orderId);
        return orderService.setOrderDeliveryAssemblyFailed(orderId);
    }

    @PostMapping("/payment")
    public OrderDto createOrderPayment(@RequestBody UUID orderId) {
        log.info("Received request to create payment order with ID:{} paid", orderId);
        return orderService.createOrderPayment(orderId);
    }

    @Override
    public OrderDto paymentSuccessful(UUID orderId) throws FeignException {
        log.info("Received request to set order status with ID:{} paid", orderId);
        return orderService.setOrderPaid(orderId);
    }

    @Override
    public OrderDto paymentFailed(UUID orderId) throws FeignException {
        log.info("Received request to set order status with ID:{} payment failed", orderId);
        return orderService.setOrderPaymentFailed(orderId);
    }

    @PostMapping("/calculate/productCost")
    public OrderDto calculateProductCost(@RequestBody UUID orderId) {
        log.info("Received request to calculate product cost for order with ID:{}", orderId);
        return orderService.calculateProductCost(orderId);
    }

    @PostMapping("/calculate/delivery")
    public OrderDto calculateDeliveryCost(@RequestBody UUID orderId) {
        log.info("Received request to calculate delivery for order with ID:{}", orderId);
        return orderService.calculateDeliveryCost(orderId);
    }

    @PostMapping("/calculate/total")
    public OrderDto calculateTotalCost(@RequestBody UUID orderId) {
        log.info("Received request to calculate total cost for order with ID:{}", orderId);
        return orderService.calculateTotalCost(orderId);
    }

    @PostMapping("/completed")
    public OrderDto completeOrder(@RequestBody UUID orderId) {
        log.info("Received request to set order status with ID:{} completed", orderId);
        return orderService.completeOrder(orderId);
    }
}