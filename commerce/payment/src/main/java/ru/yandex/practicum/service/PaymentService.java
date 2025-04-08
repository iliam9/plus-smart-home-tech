package ru.yandex.practicum.service;

import ru.yandex.practicum.model.OrderDto;
import ru.yandex.practicum.model.PaymentDto;

import java.util.UUID;

public interface PaymentService {

    PaymentDto createPayment(OrderDto order);

    double calculateProductCost(OrderDto order);

    double calculateTotalCost(OrderDto order);

    void setPaymentSuccessful(UUID paymentId);

    void setPaymentFailed(UUID paymentId);
}