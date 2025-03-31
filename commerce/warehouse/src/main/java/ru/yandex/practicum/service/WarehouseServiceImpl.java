package ru.yandex.practicum.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ShoppingStoreOperations;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.WarehouseProductMapper;
import ru.yandex.practicum.model.AddressDto;
import ru.yandex.practicum.model.BookedProductsDto;
import ru.yandex.practicum.model.QuantityState;
import ru.yandex.practicum.model.ShoppingCartDto;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.WarehouseRepository;
import ru.yandex.practicum.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.request.NewProductInWarehouseRequest;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseProductMapper warehouseProductMapper;
    private final ShoppingStoreOperations shoppingStoreOperations;
    private static final AddressDto[] ADDRESSES =
            new AddressDto[]{
                    new AddressDto("ADDRESS_1",
                            "ADDRESS_1",
                            "ADDRESS_1",
                            "ADDRESS_1",
                            "ADDRESS_1"),
                    new AddressDto("ADDRESS_2",
                            "ADDRESS_2",
                            "ADDRESS_2",
                            "ADDRESS_2",
                            "ADDRESS_2")};
    private static final AddressDto CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, 1)];


    @Override
    public void addNewProductToWarehouse(NewProductInWarehouseRequest request) {
        checkIfProductAlreadyInWarehouse(request.getProductId());
        WarehouseProduct product = warehouseProductMapper.mapToWarehouseProduct(request);
        warehouseRepository.save(product);
        log.info("Product is added to warehouse: {}", product);
    }

    @Override
    public void increaseProductQuantity(AddProductToWarehouseRequest request) {
        WarehouseProduct product = getWarehouseProduct(request.getProductId());
        int quantity = product.getQuantity();
        quantity += request.getQuantity();
        product.setQuantity(quantity);
        warehouseRepository.save(product);
        updateQuantityInShoppingStore(product);
        log.info("Added {} products with ID: {}", request.getQuantity(), request.getProductId());
    }

    @Override
    public AddressDto getWarehouseAddress() {
        log.info("Closest warehouse address: {}", CURRENT_ADDRESS);
        return CURRENT_ADDRESS;
    }

    @Override
    @Transactional(readOnly = true)
    public BookedProductsDto checkShoppingCart(ShoppingCartDto shoppingCart) {
        UUID shoppingCartId = shoppingCart.getShoppingCartId();
        Map<UUID, Integer> products = shoppingCart.getProducts();
        Supplier<Stream<WarehouseProduct>> streamSupplier =
                () -> warehouseRepository.findAllById(products.keySet()).stream();
        checkProductQuantity(streamSupplier.get(), products, shoppingCartId);
        BookedProductsDto bookedProductsDto = calculateDeliveryParams(streamSupplier);
        log.info("Delivery parameters for shopping cart ID: {} are calculated", shoppingCartId);
        return bookedProductsDto;
    }

    private WarehouseProduct getWarehouseProduct(UUID id) {
        return warehouseRepository.findById(id).orElseThrow(() -> {
            log.info("Product with ID: {} is not found in warehouse", id);
            return new NoSpecifiedProductInWarehouseException("Product is not found in warehouse");
        });
    }

    private void checkIfProductAlreadyInWarehouse(UUID id) {
        warehouseRepository.findById(id)
                .ifPresent(product -> {
                    log.warn("Product with ID: {} already exists", id);
                    throw new SpecifiedProductAlreadyInWarehouseException("Product is already in warehouse");
                });
    }

    private void checkProductQuantity(Stream<WarehouseProduct> stream, Map<UUID, Integer> products, UUID cartId) {
        if (stream.anyMatch(product -> product.getQuantity() < products.get(product.getProductId()))) {
            log.error("Quantity of products is less than necessary for shopping cart ID: {}", cartId);
            throw new ProductInShoppingCartLowQuantityInWarehouse(
                    String.format("Quantity of products is less than necessary for shopping cart ID: %s", cartId)
            );
        }
    }

    private BookedProductsDto calculateDeliveryParams(Supplier<Stream<WarehouseProduct>> streamSupplier) {
        Double deliveryVolume = streamSupplier.get()
                .map(product -> product.getWidth() * product.getHeight() * product.getDepth())
                .reduce(0.0, Double::sum);

        Double deliveryWeight = streamSupplier.get()
                .map(WarehouseProduct::getWeight)
                .reduce(0.0, Double::sum);

        boolean isFragile = streamSupplier.get().anyMatch(WarehouseProduct::isFragile);
        return new BookedProductsDto(deliveryVolume, deliveryWeight, isFragile);
    }

    private void updateQuantityInShoppingStore(WarehouseProduct product) {
        int quantity = product.getQuantity();
        QuantityState quantityState;

        if (quantity == 0) {
            quantityState = QuantityState.ENDED;
        } else if (0 < quantity && quantity <= 10) {
            quantityState = QuantityState.FEW;
        } else if (10 < quantity && quantity <= 100) {
            quantityState = QuantityState.ENOUGH;
        } else {
            quantityState = QuantityState.MANY;
        }
        try {
            shoppingStoreOperations.updateProductQuantity(product.getProductId(), quantityState);
        } catch (FeignException e) {
            log.error("Error updating product quantity in store", e);
        }
    }
}