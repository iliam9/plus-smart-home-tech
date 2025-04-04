package ru.yandex.practicum;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.model.OrderDto;

import java.util.UUID;

@FeignClient(name = "order", path = "/api/v1/order")
public interface OrderClient {

    @PostMapping("/delivery")
    OrderDto deliverySuccessful(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/delivery/failed")
    OrderDto deliveryFailed(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/delivery/assembly")
    OrderDto orderDeliveryAssembled(@RequestBody UUID orderId) throws FeignException;
}