package ru.yandex.practicum.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.DeliveryClient;
import ru.yandex.practicum.model.DeliveryDto;
import ru.yandex.practicum.model.OrderDto;
import ru.yandex.practicum.service.DeliveryService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController implements DeliveryClient {
    private final DeliveryService deliveryService;

    @Override
    public DeliveryDto planDelivery(DeliveryDto delivery) throws FeignException {
        log.info("Received request to create new delivery: {}", delivery);
        return deliveryService.createDelivery(delivery);
    }

    @Override
    public DeliveryDto finishDelivery(UUID deliveryId) throws FeignException {
        log.info("Received request to set delivery with ID:{} successful", deliveryId);
        return deliveryService.completeDelivery(deliveryId);
    }

    @Override
    public DeliveryDto deliveryFailed(UUID deliveryId) throws FeignException {
        log.info("Received request to set delivery with ID:{} failed", deliveryId);
        return deliveryService.deliveryFailed(deliveryId);
    }

    @Override
    public double calculateDeliveryCost(OrderDto order) throws FeignException {
        log.info("Received request to calculate delivery cost for order: {}", order);
        return deliveryService.calculateDeliveryCost(order);
    }

    @Override
    public DeliveryDto setDeliveryPicked(UUID deliveryId) throws FeignException {
        log.info("Received set delivery as picked, delivery ID: {}", deliveryId);
        return deliveryService.setDeliveryPicked(deliveryId);
    }
}