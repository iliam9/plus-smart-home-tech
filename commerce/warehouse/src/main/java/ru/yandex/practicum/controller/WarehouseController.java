package ru.yandex.practicum.controller;

import feign.FeignException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.WarehouseClient;
import ru.yandex.practicum.model.AddressDto;
import ru.yandex.practicum.model.BookedProductsDto;
import ru.yandex.practicum.model.ShoppingCartDto;
import ru.yandex.practicum.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.request.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.request.NewProductInWarehouseRequest;
import ru.yandex.practicum.request.ShippedToDeliveryRequest;
import ru.yandex.practicum.service.WarehouseService;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouse")
public class WarehouseController implements WarehouseClient {
    private final WarehouseService warehouseService;

    @PutMapping
    public void addProductToWarehouse(@RequestBody @Valid NewProductInWarehouseRequest request) {
        log.info("Received request to add new product in warehouse: {}", request);
        warehouseService.addNewProductToWarehouse(request);
    }

    @PostMapping("/add")
    public void increaseProductQuantity(@RequestBody @Valid AddProductToWarehouseRequest request) {
        log.info("Received request to increase product quantity of product with ID: {}", request.getProductId());
        warehouseService.increaseProductQuantity(request);
    }

    @Override
    public AddressDto getWarehouseAddress() throws FeignException {
        log.info("Received request to get closest warehouse address");
        return warehouseService.getWarehouseAddress();
    }

    @Override
    public void shippedToDelivery(ShippedToDeliveryRequest request) throws FeignException {
        log.info("Received request to ship products to delivery");
        warehouseService.shipToDelivery(request);
    }

    @Override
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request) throws FeignException {
        log.info("Received request to assembly products for order: {}", request);
        return warehouseService.assemblyProducts(request);
    }

    @Override
    public BookedProductsDto checkShoppingCart(ShoppingCartDto shoppingCartDto) throws FeignException {
        log.info("Received request to check shopping cart ID: {}", shoppingCartDto.getShoppingCartId());
        return warehouseService.checkShoppingCart(shoppingCartDto);
    }

    @Override
    public void returnProducts(Map<UUID, Integer> products) throws FeignException {
        log.info("Received request ro return products to warehouse: {}", products);
        warehouseService.returnProductsToWarehouse(products);
    }
}