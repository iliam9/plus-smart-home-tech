package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.OrderClient;
import ru.yandex.practicum.ShoppingStoreClient;
import ru.yandex.practicum.exception.NoPaymentFoundException;
import ru.yandex.practicum.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.model.OrderDto;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.model.PaymentDto;
import ru.yandex.practicum.model.PaymentState;
import ru.yandex.practicum.model.ProductDto;
import ru.yandex.practicum.repository.PaymentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final ShoppingStoreClient shoppingStoreClient;
    private final OrderClient orderClient;

    @Override
    @Transactional
    public PaymentDto createPayment(OrderDto order) {
        validatePaymentInfo(order.getProductPrice(), order.getDeliveryPrice(), order.getTotalPrice());
        Payment payment = paymentMapper.mapToPayment(order);
        payment = paymentRepository.save(payment);
        log.info("Payment created: {}", payment);
        return paymentMapper.mapToPaymentDto(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public double calculateProductCost(OrderDto order) {
        List<Double> pricesList = new ArrayList<>();
        Map<UUID, Integer> orderProducts = order.getProducts();

        orderProducts.forEach((id, quantity) -> {
            ProductDto product = shoppingStoreClient.getProductById(id);
            double totalProductPrice = product.getPrice() * quantity;
            pricesList.add(totalProductPrice);
        });

        double totalProductCost = pricesList.stream().mapToDouble(Double::doubleValue).sum();
        log.info("Total product cost is calculated: {}", totalProductCost);
        return totalProductCost;
    }

    @Override
    public double calculateTotalCost(OrderDto order) {
        validatePaymentInfo(order.getProductPrice(), order.getDeliveryPrice());
        final double VAT_RATE = 0.20;
        double productsPrice = order.getProductPrice();
        double deliveryPrice = order.getDeliveryPrice();
        double totalCost = deliveryPrice + productsPrice + (productsPrice * VAT_RATE);
        log.info("Total cost is calculated: {}", totalCost);
        return totalCost;
    }

    @Override
    @Transactional
    public void setPaymentSuccessful(UUID paymentId) {
        Payment payment = getPayment(paymentId);
        payment.setPaymentState(PaymentState.SUCCESS);
        orderClient.paymentSuccessful(payment.getOrderId());
        paymentRepository.save(payment);
        log.info("Payment with ID:{} was successful", paymentId);
    }

    @Override
    @Transactional
    public void setPaymentFailed(UUID paymentId) {
        Payment payment = getPayment(paymentId);
        payment.setPaymentState(PaymentState.FAILED);
        orderClient.paymentFailed(payment.getOrderId());
        paymentRepository.save(payment);
        log.info("Payment with ID:{} was failed", paymentId);
    }

    private Payment getPayment(UUID paymentId) {
        return paymentRepository.findById(paymentId).orElseThrow(() -> {
            log.info("Payment with ID: {} is not found", paymentId);
            return new NoPaymentFoundException("Payment is not found");
        });
    }

    private void validatePaymentInfo(Double... prices) {
        for (Double price : prices) {
            if (price == null || price == 0) {
                log.warn("Invalid payment info: one or more required values are missing");
                throw new NotEnoughInfoInOrderToCalculateException("Not enough payment info in order");
            }
        }
    }
}