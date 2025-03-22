package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.ChangeProductQuantityRequest;
import ru.yandex.practicum.model.ShoppingCartDto;
import ru.yandex.practicum.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/shopping-cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @PutMapping
    public ShoppingCartDto addProductToShoppingCart(@RequestParam String username,
                                                    @RequestBody Map<UUID, Integer> products) {
        log.info("Received request by user: {} to add products to shopping cart", username);
        return shoppingCartService.addProductToShoppingCart(username, products);
    }

    @GetMapping
    public ShoppingCartDto getUsersShoppingCart(@RequestParam String username) {
        log.info("Received request to get user's {} shopping cart", username);
        return shoppingCartService.getUsersShoppingCart(username);
    }

    @DeleteMapping
    public void deactivateShoppingCart(@RequestParam String username) {
        log.info("Received request to deactivate shopping cart of user: {}", username);
        shoppingCartService.deactivateShoppingCart(username);
    }

    @PostMapping("/remove")
    public ShoppingCartDto removeProductFromShoppingCart(@RequestParam String username,
                                                         @RequestBody List<UUID> products) {
        log.info("Received request to remove product from shopping cart of user: {}", username);
        return shoppingCartService.removeProductsFromShoppingCart(username, products);
    }

    @PostMapping("/change-quantity")
    public ShoppingCartDto changeProductQuantityInCart(@RequestParam String username,
                                                       @RequestBody ChangeProductQuantityRequest request) {
        log.info("Received request to change product quantity in shopping cart of user: {}", username);
        return shoppingCartService.changeProductQuantityInCart(username, request);
    }
}