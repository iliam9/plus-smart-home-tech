package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.model.ProductDto;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDto mapToProductDto(Product product);

    Product mapToProduct(ProductDto dto);

    Collection<ProductDto> mapToListProductDto(List<Product> products);
}