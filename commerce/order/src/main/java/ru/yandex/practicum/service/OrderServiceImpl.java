package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.WarehouseClient;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.model.BookedProductsDto;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.model.OrderDto;
import ru.yandex.practicum.model.OrderState;
import ru.yandex.practicum.repository.OrderRepository;
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

    @Override
    @Transactional
    public OrderDto createOrder(CreateNewOrderRequest request) {
        BookedProductsDto bookedProducts = warehouseClient.checkShoppingCart(request.getShoppingCart());
        Order order = orderMapper.mapToOrder(request, bookedProducts);
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
    public OrderDto returnOrderProducts(ProductReturnRequest request) {
        Order order = getOrder(request.getOrderId());
        warehouseClient.returnProducts(request.getProducts());
        order.setState(OrderState.PRODUCT_RETURNED);
        orderRepository.save(order);
        log.info("Products for order {} have been returned", request.getOrderId());
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