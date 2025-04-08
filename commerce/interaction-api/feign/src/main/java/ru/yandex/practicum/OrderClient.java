package ru.yandex.practicum;

import feign.FeignException;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.model.OrderDto;
import ru.yandex.practicum.request.CreateNewOrderRequest;
import ru.yandex.practicum.request.ProductReturnRequest;

import java.util.Collection;
import java.util.UUID;

@FeignClient(name = "order", path = "/api/v1/order")
public interface OrderClient {

    @PutMapping
    OrderDto createNewOrder(@RequestBody @Valid CreateNewOrderRequest request) throws FeignException;

    @GetMapping
    Collection<OrderDto> getUsersOrders(@RequestParam String username) throws FeignException;

    @PostMapping("/return")
    OrderDto returnOrderProducts(@RequestBody @Valid ProductReturnRequest request) throws FeignException;

    @PostMapping("/delivery")
    OrderDto deliverySuccessful(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/delivery/failed")
    OrderDto deliveryFailed(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/delivery/assembly")
    OrderDto assemblySuccessful(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/delivery/assembly/failed")
    OrderDto assemblyFailed(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/payment")
    OrderDto createOrderPayment(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/payment/successful")
    OrderDto paymentSuccessful(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/payment/failed")
    OrderDto paymentFailed(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/calculate/productCost")
    OrderDto calculateProductCost(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/calculate/delivery")
    OrderDto calculateDeliveryCost(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/calculate/total")
    OrderDto calculateTotalCost(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/completed")
    OrderDto completeOrder(@RequestBody UUID orderId) throws FeignException;
}