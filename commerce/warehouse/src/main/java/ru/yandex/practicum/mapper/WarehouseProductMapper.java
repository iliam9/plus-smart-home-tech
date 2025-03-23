package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.request.NewProductInWarehouseRequest;

@Mapper(componentModel = "spring")
public interface WarehouseProductMapper {

    @Mapping(target = "width", source = "request.dimension.width")
    @Mapping(target = "height", source = "request.dimension.height")
    @Mapping(target = "depth", source = "request.dimension.depth")
    @Mapping(target = "quantity", constant = "0")
    WarehouseProduct mapToWarehouseProduct(NewProductInWarehouseRequest request);
}