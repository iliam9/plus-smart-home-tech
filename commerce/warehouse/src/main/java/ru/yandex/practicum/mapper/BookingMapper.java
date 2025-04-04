package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.model.BookedProductsDto;
import ru.yandex.practicum.model.Booking;
import ru.yandex.practicum.request.AssemblyProductsForOrderRequest;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "fragile", source = "productsParams.fragile")
    @Mapping(target = "deliveryWeight", source = "productsParams.deliveryWeight")
    @Mapping(target = "deliveryVolume", source = "productsParams.deliveryVolume")
    @Mapping(target = "products", source = "request.products")
    @Mapping(target = "orderId", source = "request.orderId")
    Booking mapToBooking(BookedProductsDto productsParams, AssemblyProductsForOrderRequest request);

    BookedProductsDto mapToBookingDto(Booking booking);
}