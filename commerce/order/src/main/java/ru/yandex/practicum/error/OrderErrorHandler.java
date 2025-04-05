package ru.yandex.practicum.error;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.exception.ApiError;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;

@RestControllerAdvice
public class OrderErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNoOrderFound(NoOrderFoundException e) {
        return new ApiError("Order was not found", e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiError handleNotAuthorizedUser(NotAuthorizedUserException e) {
        return new ApiError("Order was not found", e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleConstraintViolationException(ConstraintViolationException e) {
        return new ApiError("Incorrectly made request", e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleArgumentNotValidException(MethodArgumentNotValidException e) {
        return new ApiError("Incorrectly made request", e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleThrowable(Exception e) {
        return new ApiError("Unexpected error", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}