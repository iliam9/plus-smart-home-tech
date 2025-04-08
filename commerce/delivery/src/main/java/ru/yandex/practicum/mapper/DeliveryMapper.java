package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.model.DeliveryDto;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    DeliveryDto mapToDeliveryDto(Delivery delivery);

    Delivery mapToDelivery(DeliveryDto dto);
}