package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.DeliveryDto;
import ru.yandex.practicum.model.OrderDto;
import ru.yandex.practicum.service.DeliveryService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;

    @PutMapping
    public DeliveryDto createDelivery(@RequestBody @Valid DeliveryDto delivery) {
        log.info("Received request to create new delivery: {}", delivery);
        return deliveryService.createDelivery(delivery);
    }

    @PostMapping("/successful")
    public DeliveryDto finishDelivery(@RequestBody UUID deliveryId) {
        log.info("Received request to set delivery with ID:{} successful", deliveryId);
        return deliveryService.completeDelivery(deliveryId);
    }

    @PostMapping("/failed")
    public DeliveryDto deliveryFailed(@RequestBody UUID deliveryId) {
        log.info("Received request to set delivery with ID:{} failed", deliveryId);
        return deliveryService.deliveryFailed(deliveryId);
    }

    @PostMapping("/cost")
    public Double calculateDeliveryCost(@RequestBody OrderDto order) {
        log.info("Received request to calculate delivery cost for order: {}", order);
        return deliveryService.calculateDeliveryCost(order);
    }

    @PostMapping("/picked")
    public DeliveryDto setDeliveryPicked(@RequestBody UUID deliveryId) {
        log.info("Received set delivery as picked, delivery ID: {}", deliveryId);
        return deliveryService.setDeliveryPicked(deliveryId);
    }
}