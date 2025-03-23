package ru.yandex.practicum.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.WarehouseOperations;
import ru.yandex.practicum.model.AddressDto;
import ru.yandex.practicum.model.BookedProductsDto;
import ru.yandex.practicum.model.ShoppingCartDto;
import ru.yandex.practicum.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.request.NewProductInWarehouseRequest;
import ru.yandex.practicum.service.WarehouseService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouse")
public class WarehouseController implements WarehouseOperations {
    private final WarehouseService warehouseService;

    @PutMapping
    public void addProductToWarehouse(@RequestBody NewProductInWarehouseRequest request) {
        log.info("Received request to add new product in warehouse: {}", request);
        warehouseService.addNewProductToWarehouse(request);
    }

    @PostMapping("/add")
    public void increaseProductQuantity(@RequestBody AddProductToWarehouseRequest request) {
        log.info("Received request to increase product quantity of product with ID: {}", request.getProductId());
        warehouseService.increaseProductQuantity(request);
    }

    @GetMapping("/address")
    public AddressDto getWarehouseAddress() {
        log.info("Received request to get closest warehouse address");
        return warehouseService.getWarehouseAddress();
    }

    @Override
    public BookedProductsDto checkShoppingCart(ShoppingCartDto shoppingCartDto) throws FeignException {
        log.info("Received request to check shopping cart ID: {}", shoppingCartDto.getShoppingCartId());
        return warehouseService.checkShoppingCart(shoppingCartDto);
    }
}