package ru.yandex.practicum.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ShoppingStoreClient;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.BookingMapper;
import ru.yandex.practicum.mapper.WarehouseProductMapper;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.BookingRepository;
import ru.yandex.practicum.repository.WarehouseRepository;
import ru.yandex.practicum.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.request.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.request.NewProductInWarehouseRequest;
import ru.yandex.practicum.request.ShippedToDeliveryRequest;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final BookingRepository bookingRepository;
    private final WarehouseProductMapper warehouseProductMapper;
    private final BookingMapper bookingMapper;
    private final ShoppingStoreClient shoppingStoreClient;
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
    @Transactional
    public void addNewProductToWarehouse(NewProductInWarehouseRequest request) {
        checkIfProductAlreadyInWarehouse(request.getProductId());
        WarehouseProduct product = warehouseProductMapper.mapToWarehouseProduct(request);
        warehouseRepository.save(product);
        log.info("Product is added to warehouse: {}", product);
    }

    @Override
    @Transactional
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
        checkProductQuantity(streamSupplier.get(), products);
        BookedProductsDto bookedProductsDto = calculateDeliveryParams(streamSupplier);
        log.info("Delivery parameters for shopping cart ID: {} are calculated", shoppingCartId);
        return bookedProductsDto;
    }

    @Override
    public void returnProductsToWarehouse(Map<UUID, Integer> products) {
        List<AddProductToWarehouseRequest> requests = products.entrySet().stream()
                .map(entry -> new AddProductToWarehouseRequest(entry.getKey(), entry.getValue()))
                .toList();

        requests.forEach(this::increaseProductQuantity);
        log.info("Products returned to warehouse");
    }

    @Override
    @Transactional
    public BookedProductsDto assemblyProducts(AssemblyProductsForOrderRequest request) {
        Map<UUID, Integer> products = request.getProducts();
        Supplier<Stream<WarehouseProduct>> streamSupplier =
                () -> warehouseRepository.findAllById(products.keySet()).stream();
        checkProductQuantity(streamSupplier.get(), products);
        BookedProductsDto bookedProductsParams = calculateDeliveryParams(streamSupplier);
        decreaseProductQuantityAfterBooking(products);

        Booking booking = bookingMapper.mapToBooking(bookedProductsParams, request);
        booking = bookingRepository.save(booking);
        log.info("Products booked for delivery: {}", booking);
        return bookingMapper.mapToBookingDto(booking);
    }

    @Override
    public void shipToDelivery(ShippedToDeliveryRequest request) {
        Booking booking = bookingRepository.findByOrderId(request.getOrderId());
        booking.setDeliveryId(request.getDeliveryId());
        bookingRepository.save(booking);
        log.info("Products shipped for delivery");
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

    private void checkProductQuantity(Stream<WarehouseProduct> stream, Map<UUID, Integer> products) {
        if (stream.anyMatch(product -> product.getQuantity() < products.get(product.getProductId()))) {
            log.error("Quantity of products is less than necessary");
            throw new ProductInShoppingCartLowQuantityInWarehouse("Quantity of products is less than necessary");
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

        BookedProductsDto productsDto = new BookedProductsDto();
        productsDto.setDeliveryVolume(deliveryVolume);
        productsDto.setDeliveryWeight(deliveryWeight);
        productsDto.setFragile(isFragile);
        return productsDto;
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
            shoppingStoreClient.updateProductQuantity(product.getProductId(), quantityState);
        } catch (FeignException e) {
            log.error("Error updating product quantity in store", e);
        }
    }

    private void decreaseProductQuantityAfterBooking(Map<UUID, Integer> products) {
        products.forEach((key, value) -> {
            WarehouseProduct product = getWarehouseProduct(key);
            int oldQuantity = product.getQuantity();
            int decreasingQuantity = value;
            product.setQuantity(oldQuantity - decreasingQuantity);
            warehouseRepository.save(product);
            updateQuantityInShoppingStore(product);
        });
    }
}