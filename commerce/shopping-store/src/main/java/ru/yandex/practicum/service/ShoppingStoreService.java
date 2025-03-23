package ru.yandex.practicum.service;

import ru.yandex.practicum.model.Pageable;
import ru.yandex.practicum.model.ProductDto;
import ru.yandex.practicum.request.SetProductQuantityStateRequest;

import java.util.Collection;
import java.util.UUID;

public interface ShoppingStoreService {

    ProductDto addProduct(ProductDto product);

    ProductDto findProductById(UUID id);

    ProductDto updateProduct(ProductDto product);

    void removeProductFromStore(UUID productId);

    void setProductQuantityState(SetProductQuantityStateRequest request);

    Collection<ProductDto> searchProducts(String category, Pageable params);

    void updateProductQuantity(SetProductQuantityStateRequest request);
}