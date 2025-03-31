package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.yandex.practicum.model.Product;

import java.util.UUID;

public interface ShoppingStoreRepository extends JpaRepository<Product, UUID>, QuerydslPredicateExecutor<Product> {

}