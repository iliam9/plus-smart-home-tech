package ru.yandex.practicum.controller;

import feign.FeignException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.ShoppingStoreClient;
import ru.yandex.practicum.model.Pageable;
import ru.yandex.practicum.model.ProductDto;
import ru.yandex.practicum.model.QuantityState;
import ru.yandex.practicum.request.SetProductQuantityStateRequest;
import ru.yandex.practicum.service.ShoppingStoreService;

import java.util.Collection;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/shopping-store")
public class ShoppingStoreController implements ShoppingStoreClient {
    private final ShoppingStoreService shoppingStoreService;

    @PutMapping
    public ProductDto addProduct(@Valid @RequestBody ProductDto product) {
        log.info("Received request to add product to store: {}", product);
        return shoppingStoreService.addProduct(product);
    }

    @Override
    public ProductDto getProductById(UUID id) throws FeignException {
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

    @GetMapping
    public Collection<ProductDto> searchProducts(String category, Pageable params) {
        log.info("Received request to search products from category {} with params: {}", category, params);
        return shoppingStoreService.searchProducts(category, params);
    }

    @Override
    public void updateProductQuantity(UUID productId, QuantityState quantityState) throws FeignException {
        SetProductQuantityStateRequest request = new SetProductQuantityStateRequest(productId, quantityState);
        log.info("Received request to update product quantity: {}", request);
        shoppingStoreService.setProductQuantityState(request);
    }
}