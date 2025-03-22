package ru.yandex.practicum.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.model.ChangeProductQuantityRequest;
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
@Transactional(readOnly = true)
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;

    @Override
    @Transactional
    public ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Integer> products) {
        validateUsername(username);
        ShoppingCart shoppingCart = getShoppingCart(username);
        Map<UUID, Integer> oldProducts = shoppingCart.getProducts();
        oldProducts.putAll(products);
        shoppingCart.setProducts(oldProducts);
        shoppingCartRepository.save(shoppingCart);
        log.info("Products added to shopping cart: {}", shoppingCart);
        return shoppingCartMapper.mapToShoppingCartDto(shoppingCart);
    }

    @Override
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
        productMap.keySet().retainAll(products);
        shoppingCart.setProducts(productMap);
        shoppingCartRepository.save(shoppingCart);
        log.info("Product in shopping cart of user {} have been changed", username);
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