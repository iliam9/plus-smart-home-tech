package ru.yandex.practicum;

import feign.FeignException;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.model.DeliveryDto;
import ru.yandex.practicum.model.OrderDto;

import java.util.UUID;


@FeignClient(name = "delivery", path = "/api/v1/delivery")
public interface DeliveryClient {

    @PutMapping
    DeliveryDto planDelivery(@RequestBody @Valid DeliveryDto delivery) throws FeignException;

    @PostMapping("/successful")
    DeliveryDto finishDelivery(@RequestBody UUID deliveryId) throws FeignException;

    @PostMapping("/failed")
    DeliveryDto deliveryFailed(@RequestBody UUID deliveryId) throws FeignException;

    @PostMapping("/cost")
    double calculateDeliveryCost(@RequestBody @Valid OrderDto order) throws FeignException;

    @PostMapping("/picked")
    DeliveryDto setDeliveryPicked(@RequestBody UUID deliveryId) throws FeignException;
}