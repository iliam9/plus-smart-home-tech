package ru.yandex.practicum;


import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.model.QuantityState;

import java.util.UUID;


@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store")
public interface ShoppingStoreOperations {

    @PostMapping("/quantityState")
    void updateProductQuantity(@RequestParam UUID productId,
                               @RequestParam QuantityState quantityState) throws FeignException;
}