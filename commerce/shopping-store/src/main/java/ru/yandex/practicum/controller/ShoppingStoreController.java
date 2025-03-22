package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.Pageable;
import ru.yandex.practicum.model.ProductCategory;
import ru.yandex.practicum.model.ProductDto;
import ru.yandex.practicum.request.SetProductQuantityStateRequest;
import ru.yandex.practicum.service.ShoppingStoreService;

import java.util.Collection;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/shopping-store")
public class ShoppingStoreController {
    private final ShoppingStoreService shoppingStoreService;

    @PutMapping
    public ProductDto addProduct(@Valid @RequestBody ProductDto product) {
        log.info("Received request to add product to store: {}", product);
        return shoppingStoreService.addProduct(product);
    }

    @GetMapping("/{id}")
    public ProductDto getProductById(@PathVariable UUID id) {
        log.info("Received request to get product by ID: {}", id);
        return shoppingStoreService.findProductById(id);
    }

    @PostMapping
    public ProductDto updateProduct(@Valid @RequestBody ProductDto product) {
        log.info("Received request to update product: {}", product);
        return shoppingStoreService.updateProduct(product);
    }

    @PostMapping("/removeProductFromStore")
    public void removeProduct(@RequestBody UUID productId) {
        log.info("Received request to remove from store product with ID: {}", productId);
        shoppingStoreService.removeProductFromStore(productId);
    }

    @PostMapping("/quantityState")
    public void setProductQuantity(SetProductQuantityStateRequest request) {
        log.info("Received request to change product with ID: {} quantity state to: {}",
                request.getProductId(), request.getQuantityState());
        shoppingStoreService.setProductQuantityState(request);
    }

    @GetMapping
    public Collection<ProductDto> searchProducts(String category, Pageable params) {
        log.info("Received request to search products from category {} with params: {}", category, params);
        return shoppingStoreService.searchProducts(category, params);
    }
}