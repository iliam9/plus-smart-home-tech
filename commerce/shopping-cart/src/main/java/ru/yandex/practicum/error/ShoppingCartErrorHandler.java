package ru.yandex.practicum.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.exception.ApiError;
import ru.yandex.practicum.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.exception.ProductInShoppingCartNotInWarehouse;

@Slf4j
@RestControllerAdvice
public class ShoppingCartErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiError handleNotAuthorizedUser(NotAuthorizedUserException exception) {
        return new ApiError(HttpStatus.UNAUTHORIZED, exception, "User is not authorized");
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleNoProductsInShoppingCart(NoProductsInShoppingCartException exception) {
        return new ApiError(HttpStatus.BAD_REQUEST, exception, "No products in shopping cart");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleProductInShoppingCartNotInWarehouse(ProductInShoppingCartNotInWarehouse exception) {
        return new ApiError(HttpStatus.BAD_REQUEST, exception, "Selected products not in warehouse");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleThrowable(Exception exception) {
        return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, exception, "Unexpected error");
    }
}