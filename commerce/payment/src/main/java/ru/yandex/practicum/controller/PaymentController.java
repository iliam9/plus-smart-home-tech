package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.model.OrderDto;
import ru.yandex.practicum.model.PaymentDto;
import ru.yandex.practicum.service.PaymentService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public PaymentDto createPayment(@RequestBody @Valid OrderDto order) {
        log.info("Received request to create payment for order with ID:{}", order.getOrderId());
        return paymentService.createPayment(order);
    }
}