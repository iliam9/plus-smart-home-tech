package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.DeliveryClient;
import ru.yandex.practicum.PaymentClient;
import ru.yandex.practicum.WarehouseClient;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.model.AddressDto;
import ru.yandex.practicum.model.BookedProductsDto;
import ru.yandex.practicum.model.DeliveryDto;
import ru.yandex.practicum.model.DeliveryState;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.model.OrderDto;
import ru.yandex.practicum.model.OrderState;
import ru.yandex.practicum.model.PaymentDto;
import ru.yandex.practicum.repository.OrderRepository;
import ru.yandex.practicum.request.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.request.CreateNewOrderRequest;
import ru.yandex.practicum.request.ProductReturnRequest;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final WarehouseClient warehouseClient;
    private final DeliveryClient deliveryClient;
    private final PaymentClient paymentClient;

    @Override
    @Transactional
    public OrderDto createOrder(CreateNewOrderRequest request) {
        BookedProductsDto bookedProducts = warehouseClient.checkShoppingCart(request.getShoppingCart());
        Order order = orderMapper.mapToOrder(request, bookedProducts);
        order = orderRepository.save(order);

        AddressDto warehouseAddress = warehouseClient.getWarehouseAddress();
        DeliveryDto newDelivery = DeliveryDto.builder()
                .fromAddress(warehouseAddress)
                .toAddress(request.getDeliveryAddress())
                .orderId(order.getOrderId())
                .deliveryState(DeliveryState.CREATED)
                .build();
        newDelivery = deliveryClient.planDelivery(newDelivery);
        order.setDeliveryId(newDelivery.getDeliveryId());

        order = orderRepository.save(order);
        log.info("New order is saved: {}", order);
        return orderMapper.mapToOrderDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<OrderDto> getUsersOrders(String username) {
        validateUsername(username);
        List<Order> orders = orderRepository.findByUsername(username);
        log.info("Orders of user {} are found", username);
        return orderMapper.mapToListOrderDto(orders);
    }

    @Override
    @Transactional
    public OrderDto returnOrderProducts(ProductReturnRequest request) {
        Order order = getOrder(request.getOrderId());
        warehouseClient.returnProducts(request.getProducts());
        order.setState(OrderState.PRODUCT_RETURNED);
        orderRepository.save(order);
        log.info("Products for order {} have been returned", request.getOrderId());
        return orderMapper.mapToOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto setOrderDeliverySuccessful(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.DELIVERED);
        orderRepository.save(order);
        log.info("Order with ID:{} is successfully delivered", orderId);
        return orderMapper.mapToOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto setOrderDeliveryFailed(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.DELIVERY_FAILED);
        orderRepository.save(order);
        log.info("Delivery for order with ID:{} failed", orderId);
        return orderMapper.mapToOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto setOrderDeliveryInProgress(UUID orderId) {
        Order order = getOrder(orderId);

        AssemblyProductsForOrderRequest request = AssemblyProductsForOrderRequest.builder()
                .orderId(order.getOrderId())
                .products(order.getProducts())
                .build();
        warehouseClient.assemblyProductsForOrder(request);

        order.setState(OrderState.ASSEMBLED);
        order = orderRepository.save(order);
        log.info("Delivery for order with ID:{} was picked up", orderId);
        return orderMapper.mapToOrderDto(order);
    }

    @Override
    public OrderDto setOrderDeliveryAssemblyFailed(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.ASSEMBLY_FAILED);
        order = orderRepository.save(order);
        log.info("Assembly for order with ID:{} was failed", orderId);
        return orderMapper.mapToOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto createOrderPayment(UUID orderId) {
        Order order = getOrder(orderId);
        PaymentDto payment = paymentClient.createPayment(orderMapper.mapToOrderDto(order));
        order.setPaymentId(payment.getPaymentId());
        order.setState(OrderState.ON_PAYMENT);
        order = orderRepository.save(order);
        log.info("Payment for order with ID:{} was created", orderId);
        return orderMapper.mapToOrderDto(order);
    }

    @Override
    public OrderDto setOrderPaid(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.PAID);
        order = orderRepository.save(order);
        log.info("Order with ID:{} was successfully paid", orderId);
        return orderMapper.mapToOrderDto(order);
    }

    @Override
    public OrderDto setOrderPaymentFailed(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.PAYMENT_FAILED);
        order = orderRepository.save(order);
        log.info("Payment for order with ID:{} was failed", orderId);
        return orderMapper.mapToOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto calculateProductCost(UUID orderId) {
        Order order = getOrder(orderId);
        double productPrice = paymentClient.calculateProductCost(orderMapper.mapToOrderDto(order));
        order.setProductPrice(productPrice);
        order = orderRepository.save(order);
        log.info("Product cost for order with ID:{} is calculated:{}", orderId, productPrice);
        return orderMapper.mapToOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto calculateDeliveryCost(UUID orderId) {
        Order order = getOrder(orderId);
        double deliveryPrice = deliveryClient.calculateDeliveryCost(orderMapper.mapToOrderDto(order));
        order.setDeliveryPrice(deliveryPrice);
        order = orderRepository.save(order);
        log.info("Delivery cost for order with ID:{} is calculated:{}", orderId, deliveryPrice);
        return orderMapper.mapToOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto calculateTotalCost(UUID orderId) {
        Order order = getOrder(orderId);
        double totalPrice = paymentClient.calculateTotalCost(orderMapper.mapToOrderDto(order));
        order.setTotalPrice(totalPrice);
        order = orderRepository.save(order);
        log.info("Total price for order with ID:{} is calculated:{}", orderId, totalPrice);
        return orderMapper.mapToOrderDto(order);
    }

    @Override
    public OrderDto completeOrder(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.COMPLETED);
        order = orderRepository.save(order);
        log.info("Order with ID:{} is completed", orderId);
        return orderMapper.mapToOrderDto(order);
    }

    private Order getOrder(UUID id) {
        return orderRepository.findById(id).orElseThrow(() -> {
            log.info("Order with ID: {} is not found", id);
            return new NoOrderFoundException("Order is not found");
        });
    }

    private void validateUsername(String username) {
        if (username.isBlank()) {
            throw new NotAuthorizedUserException("Username is blank");
        }
    }
}