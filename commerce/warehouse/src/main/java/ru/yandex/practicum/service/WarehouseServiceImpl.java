package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.mapper.WarehouseProductMapper;
import ru.yandex.practicum.repository.WarehouseRepository;
import ru.yandex.practicum.request.NewProductInWarehouseRequest;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseProductMapper warehouseProductMapper;


    @Override
    public void addNewProductToWarehouse(NewProductInWarehouseRequest request) {

    }
}