package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.model.DeliveryDto;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    DeliveryDto mapToDeliveryDto(Delivery delivery);

    @Mapping(target = "deliveryId", ignore = true)
    Delivery mapToDelivery(DeliveryDto dto);
}