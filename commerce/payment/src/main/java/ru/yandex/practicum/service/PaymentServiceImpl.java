package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.model.OrderDto;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.model.PaymentDto;
import ru.yandex.practicum.repository.PaymentRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentDto createPayment(OrderDto order) {
        Payment payment = paymentMapper.mapToPayment(order);
        payment = paymentRepository.save(payment);
        //TODO return OrderDto wight paymentId in it
        log.info("Payment created: {}", payment);
        return paymentMapper.mapToPaymentDto(payment);
    }


}