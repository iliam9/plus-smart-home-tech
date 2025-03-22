package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.ShoppingCart;

import java.util.UUID;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, UUID> {
}