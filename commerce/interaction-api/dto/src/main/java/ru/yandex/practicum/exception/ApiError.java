package ru.yandex.practicum.exception;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiError {
    String message;
    String reason;
    HttpStatus status;
    Instant timestamp;

    public ApiError(String message, String reason, HttpStatus status) {
        this.message = message;
        this.reason = reason;
        this.status = status;
        timestamp = Instant.now();
    }
}