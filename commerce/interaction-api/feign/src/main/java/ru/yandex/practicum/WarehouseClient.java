package ru.yandex.practicum;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.model.BookedProductsDto;
import ru.yandex.practicum.model.ShoppingCartDto;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse")
public interface WarehouseClient {

    @PostMapping("/check")
    BookedProductsDto checkShoppingCart(@RequestBody ShoppingCartDto shoppingCartDto) throws FeignException;

    @PostMapping("/return")
    void returnProducts(@RequestBody Map<UUID, Integer> products) throws FeignException;
}