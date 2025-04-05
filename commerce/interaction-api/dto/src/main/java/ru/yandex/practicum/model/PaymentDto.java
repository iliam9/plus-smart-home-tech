package ru.yandex.practicum.model;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentDto {

    @NotNull
    UUID paymentId;

    @NotNull
    UUID orderId;

    @NotNull
    Double totalPayment;

    @NotNull
    Double deliveryTotal;

    @NotNull
    Double feeTotal;

    PaymentState paymentState;
}