package ru.yandex.practicum.exception;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NoProductsInShoppingCartException extends RuntimeException {
    String httpStatus;
    String userMessage;
    String message;
    StackTraceElement[] stackTrace;
    Throwable cause;

    public NoProductsInShoppingCartException(String message) {
        super(message);
        this.stackTrace = Thread.currentThread().getStackTrace();
    }
}