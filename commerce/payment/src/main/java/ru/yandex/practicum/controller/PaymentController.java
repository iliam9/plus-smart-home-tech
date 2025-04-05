package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.PaymentClient;
import ru.yandex.practicum.model.OrderDto;
import ru.yandex.practicum.model.PaymentDto;
import ru.yandex.practicum.service.PaymentService;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController implements PaymentClient {
    private final PaymentService paymentService;

    @Override
    public PaymentDto createPayment(OrderDto order) {
        log.info("Received request to create payment for order with ID:{}", order.getOrderId());
        return paymentService.createPayment(order);
    }

    @Override
    public double calculateProductCost(OrderDto order) {
        log.info("Received request to calculate product cost for order with ID:{}", order.getOrderId());
        return paymentService.calculateProductCost(order);
    }

    @Override
    public double calculateTotalCost(OrderDto order) {
        log.info("Received request to calculate total cost for order with ID:{}", order.getOrderId());
        return paymentService.calculateTotalCost(order);
    }

    @PostMapping("/refund")
    public void setPaymentSuccessful(@RequestBody UUID paymentId) {
        log.info("Received request to set payment with ID:{} successful", paymentId);
        paymentService.setPaymentSuccessful(paymentId);
    }

    @PostMapping("/failed")
    public void setPaymentFailed(@RequestBody UUID paymentId) {
        log.info("Received request to set payment with ID:{} failed", paymentId);
        paymentService.setPaymentFailed(paymentId);
    }
}