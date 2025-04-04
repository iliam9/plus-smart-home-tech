package ru.yandex.practicum.service;

import ru.yandex.practicum.model.OrderDto;
import ru.yandex.practicum.model.PaymentDto;

public interface PaymentService {

    PaymentDto createPayment(OrderDto order);
}