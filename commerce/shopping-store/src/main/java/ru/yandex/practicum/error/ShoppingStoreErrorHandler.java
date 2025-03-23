package ru.yandex.practicum.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.exception.ApiError;
import ru.yandex.practicum.exception.ProductNotFoundException;

@Slf4j
@RestControllerAdvice
public class ShoppingStoreErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleProductNotFound(ProductNotFoundException exception) {
        return new ApiError(HttpStatus.NOT_FOUND, exception, "The required object was not found.");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleThrowable(Exception exception) {
        return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, exception, "Unexpected error");
    }
}