package ru.yandex.practicum.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.ShoppingStoreRepository;
import ru.yandex.practicum.request.SetProductQuantityStateRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ShoppingStoreServiceImpl implements ShoppingStoreService {
    private final ShoppingStoreRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductDto addProduct(ProductDto productDto) {
        Product product = productMapper.mapToProduct(productDto);
        product = productRepository.save(product);
        log.info("Product is saved: {}", product);
        return productMapper.mapToProductDto(product);
    }

    @Override
    public ProductDto findProductById(UUID id) {
        Product product = getProduct(id);
        log.info("Product is found: {}", product);
        return productMapper.mapToProductDto(product);
    }

    @Override
    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        getProduct(productDto.getProductId());
        Product productUpdated = productMapper.mapToProduct(productDto);
        productUpdated = productRepository.save(productUpdated);
        log.info("Product is updated: {}", productUpdated);
        return productMapper.mapToProductDto(productUpdated);
    }

    @Override
    @Transactional
    public void removeProductFromStore(UUID productId) {
        Product product = getProduct(productId);
        product.setProductState(ProductState.DEACTIVATE);
        productRepository.save(product);
        log.info("Product with ID: {} removed from store", productId);
    }

    @Override
    @Transactional
    public void setProductQuantityState(SetProductQuantityStateRequest request) {
        Product product = getProduct(request.getProductId());
        product.setQuantityState(request.getQuantityState());
        productRepository.save(product);
        log.info("Product quantity is changed to {}", request.getQuantityState());
    }

    @Override
    public Collection<ProductDto> searchProducts(String category, Pageable params) {
        BooleanBuilder query = buildSearchQuery(category);
        Sort sort = createSort(params.getSort());
        PageRequest pageable = PageRequest.of(params.getPage(), params.getSize(), sort);
        List<Product> products = productRepository.findAll(query, pageable).getContent();
        log.info("Search is complete");
        return productMapper.mapToListProductDto(products);
    }

    @Override
    public void updateProductQuantity(SetProductQuantityStateRequest request) {
        Product product = getProduct(request.getProductId());
        product.setQuantityState(request.getQuantityState());
        productRepository.save(product);
        log.info("Updated quantity of product with ID: {}", request.getProductId());
    }

    private Product getProduct(UUID id) {
        return productRepository.findById(id).orElseThrow(() -> {
            log.info("Product with ID: {} is not found", id);
            return new ProductNotFoundException("Product is not found");
        });
    }

    private BooleanBuilder buildSearchQuery(String categoryToParse) {
        ProductCategory category = ProductCategory.valueOf(categoryToParse);
        BooleanBuilder searchParams = new BooleanBuilder();
        searchParams.and(QProduct.product.productCategory.stringValue().eq(category.toString()));
        return searchParams;
    }

    private Sort createSort(List<String> sortFields) {
        List<Sort.Order> orders = new ArrayList<>();
        for (String sortField : sortFields) {
            orders.add(Sort.Order.asc(sortField));
        }
        return Sort.by(orders);
    }
}