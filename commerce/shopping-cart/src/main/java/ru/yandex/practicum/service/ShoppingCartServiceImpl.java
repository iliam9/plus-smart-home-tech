package ru.yandex.practicum.service;


import feign.FeignException;
import jakarta.ws.rs.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.WarehouseOperations;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.exception.ProductInShoppingCartNotInWarehouse;
import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.model.BookedProductsDto;
import ru.yandex.practicum.request.ChangeProductQuantityRequest;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.model.ShoppingCartDto;
import ru.yandex.practicum.model.ShoppingCartState;
import ru.yandex.practicum.repository.ShoppingCartRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final WarehouseOperations warehouseClient;

    @Override
    @Transactional
    public ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Integer> products) {
        validateUsername(username);
        ShoppingCart shoppingCart = getShoppingCart(username);
        Map<UUID, Integer> oldProducts = shoppingCart.getProducts();
        oldProducts.putAll(products);
        shoppingCart.setProducts(oldProducts);
        log.info("Sending request to check shopping cart: {}", shoppingCartMapper.mapToShoppingCartDto(shoppingCart));
        warehouseClient.checkShoppingCart(shoppingCartMapper.mapToShoppingCartDto(shoppingCart));
        shoppingCartRepository.save(shoppingCart);
        log.info("Products added to shopping cart: {}", shoppingCart);
        return shoppingCartMapper.mapToShoppingCartDto(shoppingCart);
    }

    @Override
    @Transactional(readOnly = true)
    public ShoppingCartDto getUsersShoppingCart(String username) {
        validateUsername(username);
        ShoppingCart shoppingCart = getShoppingCart(username);
        log.info("Returning shopping cart of user {}", username);
        return shoppingCartMapper.mapToShoppingCartDto(shoppingCart);
    }

    @Override
    @Transactional
    public void deactivateShoppingCart(String username) {
        validateUsername(username);
        ShoppingCart shoppingCart = getShoppingCart(username);
        shoppingCart.setState(ShoppingCartState.DEACTIVATE);
        shoppingCartRepository.save(shoppingCart);
        log.info("Shopping cart of user {} is deactivated", username);
    }

    @Override
    @Transactional
    public ShoppingCartDto removeProductsFromShoppingCart(String username, List<UUID> products) {
        validateUsername(username);
        ShoppingCart shoppingCart = getShoppingCart(username);
        Map<UUID, Integer> productMap = shoppingCart.getProducts();
        products.forEach(productMap::remove);
        shoppingCart.setProducts(productMap);
        shoppingCartRepository.save(shoppingCart);
        log.info("Product in shopping cart of user {} have been removed", username);
        return shoppingCartMapper.mapToShoppingCartDto(shoppingCart);
    }

    @Override
    @Transactional
    public ShoppingCartDto changeProductQuantityInCart(String username, ChangeProductQuantityRequest request) {
        validateUsername(username);
        ShoppingCart shoppingCart = getShoppingCart(username);
        Map<UUID, Integer> cartProducts = shoppingCart.getProducts();
        cartProducts.put(request.getProductId(), request.getNewQuantity());
        shoppingCart.setProducts(cartProducts);
        shoppingCartRepository.save(shoppingCart);
        log.info("Product quantity is changed to {}", request.getNewQuantity());
        return shoppingCartMapper.mapToShoppingCartDto(shoppingCart);
    }

    @Override
    @Transactional
    public BookedProductsDto bookProductFromShoppingCart(String username) {
        validateUsername(username);
        ShoppingCart shoppingCart = getShoppingCart(username);

        try {
            ShoppingCartDto shoppingCartDto = shoppingCartMapper.mapToShoppingCartDto(shoppingCart);
            BookedProductsDto bookedProductsDto = warehouseClient.checkShoppingCart(shoppingCartDto);
            log.info("Booked product from shopping cart ID: {}", shoppingCart.getShoppingCartId());
            return bookedProductsDto;

        } catch (FeignException e) {
            if (e.status() == 400) {
                throw new ProductInShoppingCartNotInWarehouse(
                        String.format("Product from shopping cart ID: %s not in warehouse",
                                shoppingCart.getShoppingCartId()));
            } else {
                throw new InternalServerErrorException("Service warehouse is not available");
            }
        }
    }

    private void validateUsername(String username) {
        if (username.isBlank()) {
            throw new NotAuthorizedUserException("Username is blank");
        }
    }

    private ShoppingCart getShoppingCart(String username) {
        return shoppingCartRepository.findByUsernameAndState(username, ShoppingCartState.ACTIVE)
                .orElseGet(() -> {
                    ShoppingCart newShoppingCart = new ShoppingCart();
                    newShoppingCart.setUsername(username);
                    newShoppingCart.setProducts(new HashMap<>());
                    newShoppingCart.setState(ShoppingCartState.ACTIVE);
                    return shoppingCartRepository.save(newShoppingCart);
                });
    }
}