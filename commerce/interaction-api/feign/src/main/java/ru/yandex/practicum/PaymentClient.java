package ru.yandex.practicum;

import feign.FeignException;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.model.OrderDto;
import ru.yandex.practicum.model.PaymentDto;

@FeignClient(name = "payment", path = "/api/v1/payment")
public interface PaymentClient {

    @PostMapping
    PaymentDto createPayment(@RequestBody @Valid OrderDto order) throws FeignException;

    @PostMapping("/totalCost")
    double calculateTotalCost(@RequestBody @Valid OrderDto order) throws FeignException;

    @PostMapping("/productCost")
    double calculateProductCost(@RequestBody @Valid OrderDto order) throws FeignException;
}