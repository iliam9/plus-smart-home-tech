package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.OrderClient;
import ru.yandex.practicum.WarehouseClient;
import ru.yandex.practicum.exception.NoDeliveryFoundException;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.DeliveryRepository;
import ru.yandex.practicum.request.ShippedToDeliveryRequest;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;

    @Override
    @Transactional
    public DeliveryDto createDelivery(DeliveryDto deliveryDto) {
        AddressDto warehouseAddress = warehouseClient.getWarehouseAddress();
        deliveryDto.setFromAddress(warehouseAddress);
        Delivery delivery = deliveryMapper.mapToDelivery(deliveryDto);
        delivery.setDeliveryState(DeliveryState.CREATED);
        delivery = deliveryRepository.save(delivery);
        log.info("New delivery is created: {}", delivery);
        return deliveryMapper.mapToDeliveryDto(delivery);
    }

    @Override
    @Transactional
    public DeliveryDto completeDelivery(UUID deliveryId) {
        Delivery delivery = getDelivery(deliveryId);
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        delivery = deliveryRepository.save(delivery);
        orderClient.deliverySuccessful(delivery.getOrderId());
        log.info("Delivery with ID:{} successfully shipped", deliveryId);
        return deliveryMapper.mapToDeliveryDto(delivery);
    }

    @Override
    @Transactional
    public DeliveryDto deliveryFailed(UUID deliveryId) {
        Delivery delivery = getDelivery(deliveryId);
        delivery.setDeliveryState(DeliveryState.FAILED);
        delivery = deliveryRepository.save(delivery);
        orderClient.deliveryFailed(delivery.getOrderId());
        log.info("Delivery with ID:{} failed", deliveryId);
        return deliveryMapper.mapToDeliveryDto(delivery);
    }

    @Override
    public Double calculateDeliveryCost(OrderDto order) {
        final double BASE_RATE = 5.0;
        final double WAREHOUSE_1_ADDRESS_MULTIPLIER = 1;
        final double WAREHOUSE_2_ADDRESS_MULTIPLIER = 2;
        final double FRAGILE_MULTIPLIER = 0.2;
        final double WEIGHT_MULTIPLIER = 0.3;
        final double VOLUME_MULTIPLIER = 0.2;
        final double STREET_MULTIPLIER = 0.2;

        Delivery delivery = getDelivery(order.getDeliveryId());
        Address warehouseAddress = delivery.getFromAddress();
        Address destinationAddress = delivery.getToAddress();

        double totalCost = BASE_RATE;

        totalCost += warehouseAddress.getCity().equals("ADDRESS_1")
                ? totalCost * WAREHOUSE_1_ADDRESS_MULTIPLIER : totalCost * WAREHOUSE_2_ADDRESS_MULTIPLIER;

        totalCost += Boolean.TRUE.equals(order.getFragile()) ? totalCost * FRAGILE_MULTIPLIER : 0;

        totalCost += order.getDeliveryWeight() * WEIGHT_MULTIPLIER;

        totalCost += order.getDeliveryVolume() * VOLUME_MULTIPLIER;

        totalCost += warehouseAddress.getStreet().equals(destinationAddress.getStreet())
                ? 0 : totalCost * STREET_MULTIPLIER;

        log.info("Delivery cost calculated: {}", totalCost);
        return totalCost;
    }

    @Override
    @Transactional
    public DeliveryDto setDeliveryPicked(UUID deliveryId) {
        Delivery delivery = getDelivery(deliveryId);
        UUID orderId = delivery.getOrderId();
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        orderClient.orderDeliveryAssembled(orderId);
        warehouseClient.shippedToDelivery(new ShippedToDeliveryRequest(orderId, deliveryId));
        delivery = deliveryRepository.save(delivery);
        log.info("Delivery with ID:{} is in progress", deliveryId);
        return deliveryMapper.mapToDeliveryDto(delivery);
    }

    private Delivery getDelivery(UUID id) {
        return deliveryRepository.findById(id).orElseThrow(() -> {
            log.info("Delivery with ID: {} is not found", id);
            return new NoDeliveryFoundException("Delivery is not found");
        });
    }
}