package ru.yandex.practicum.exception;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductNotFoundException extends RuntimeException {
  String httpStatus;
  String userMessage;
  String message;
  StackTraceElement[] stackTrace;
  Throwable cause;

  public ProductNotFoundException(String message, String httpStatus, String userMessage) {
    super(message);
    this.httpStatus = httpStatus;
    this.userMessage = userMessage;
    this.message = message;
    this.stackTrace = Thread.currentThread().getStackTrace();
  }

  public ProductNotFoundException(String message, Throwable cause, String httpStatus, String userMessage) {
    super(message, cause);
    this.cause = cause;
    this.httpStatus = httpStatus;
    this.userMessage = userMessage;
    this.message = message;
    this.stackTrace = Thread.currentThread().getStackTrace();
  }
}