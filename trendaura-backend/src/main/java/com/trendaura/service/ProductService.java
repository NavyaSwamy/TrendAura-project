package com.trendaura.service;

import com.trendaura.dto.ProductRequest;
import com.trendaura.dto.ProductResponse;
import com.trendaura.entity.Product;
import com.trendaura.entity.User;
import com.trendaura.exception.ResourceNotFoundException;
import com.trendaura.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    // =====================================================
    // ⭐ CREATE PRODUCT
    // =====================================================
    public ProductResponse addProduct(ProductRequest request, User user) {

        validateRequest(request);

        try {

            Product product = new Product();

            product.setName(request.getName().trim());
            product.setDescription(request.getDescription());
            product.setPrice(request.getPrice());
            product.setStateCode(request.getStateCode());
            product.setFashionType(request.getFashionType());
            product.setCategory(StringUtils.hasText(request.getCategory()) ? request.getCategory() : request.getFashionType());
            product.setSellerName(request.getSellerName());
            product.setAvailableSizes(request.getAvailableSizes());
            product.setAvailableColors(request.getAvailableColors());
            product.setExtraImageUrls(request.getExtraImageUrls());
            product.setStock(request.getStock() != null ? request.getStock() : 0);

            if (user != null) {
                product.setSellerName(user.getFullName());
            }

            product.setActive(true);
            product.setCreatedAt(Instant.now());
            product.setUpdatedAt(Instant.now());

            if (StringUtils.hasText(request.getImageUrl())) {
                product.setImageUrl(request.getImageUrl().trim());
            }

            Product saved = productRepository.save(product);
            return toResponse(saved);

        } catch (Exception e) {
            log.error("Product creation failed", e);
            throw new RuntimeException("Product creation failed");
        }
    }

    // =====================================================
    // ⭐ ADVANCED SEARCH (FULL FILTER ENGINE)
    // =====================================================
    public List<ProductResponse> advancedSearch(
            String keyword,
            String category,
            String stateCode,
            String fashionType,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size);

        return productRepository.findByActiveTrue(pageable)
                .stream()
                .filter(p -> !StringUtils.hasText(keyword) ||
                        p.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                        (p.getDescription() != null &&
                                p.getDescription().toLowerCase().contains(keyword.toLowerCase())))
                .filter(p -> !StringUtils.hasText(category) ||
                        category.equalsIgnoreCase(p.getCategory()))
                .filter(p -> !StringUtils.hasText(stateCode) ||
                        stateCode.equalsIgnoreCase(p.getStateCode()))
                .filter(p -> !StringUtils.hasText(fashionType) ||
                        fashionType.equalsIgnoreCase(p.getFashionType()))
                .map(ProductService::toResponseStatic)
                .collect(Collectors.toList());
    }

    // =====================================================
    // ⭐ SEARCH BY STATE + TYPE
    // =====================================================
    public List<ProductResponse> searchByStateAndType(
            String stateCode,
            String fashionType,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size);

        return productRepository.findByActiveTrue(pageable)
                .stream()
                .filter(p -> !StringUtils.hasText(stateCode) ||
                        stateCode.equalsIgnoreCase(p.getStateCode()))
                .filter(p -> !StringUtils.hasText(fashionType) ||
                        fashionType.equalsIgnoreCase(p.getFashionType()))
                .map(ProductService::toResponseStatic)
                .collect(Collectors.toList());
    }

    // =====================================================
    // ⭐ LIST PRODUCTS
    // =====================================================
    public Page<ProductResponse> listAll(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(
                    Math.max(page, 0),
                    Math.min(size, 50),
                    Sort.by(Sort.Direction.DESC, "createdAt")
            );

            Page<Product> productPage = productRepository.findAll(pageable);
            return productPage.map(this::toResponse);
        } catch (Exception e) {
            log.error("Error in listAll", e);
            throw e;
        }
    }

    // =====================================================
    // ⭐ TRENDING PRODUCTS
    // =====================================================
    public List<ProductResponse> getTrendingProducts() {

        Pageable pageable = PageRequest.of(0, 12);

        return productRepository
                .findByActiveTrueOrderByCreatedAtDesc(pageable)
                .stream()
                .map(ProductService::toResponseStatic)
                .collect(Collectors.toList());
    }

    // =====================================================
    // ⭐ RECOMMENDATION HOOK
    // =====================================================
    public List<ProductResponse> getRecommendedProducts(User user) {

        Pageable pageable = PageRequest.of(0, 10);

        return productRepository
                .findByActiveTrue(pageable)
                .stream()
                .map(ProductService::toResponseStatic)
                .collect(Collectors.toList());
    }

    // =====================================================
    // ⭐ GET BY ID
    // =====================================================
    public ProductResponse getById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found"));

        return toResponse(product);
    }

    // =====================================================
    // ⭐ SOFT DELETE
    // =====================================================
    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found"));

        product.setActive(false);
        product.setUpdatedAt(Instant.now());

        productRepository.save(product);
    }

    // =====================================================
    // ⭐ VALIDATION
    // =====================================================
    private void validateRequest(ProductRequest request) {

        if (!StringUtils.hasText(request.getName())) {
            throw new RuntimeException("Product name required");
        }

        if (request.getPrice() == null ||
                request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Invalid product price");
        }
    }

    // =====================================================
    // MAPPER
    // =====================================================
    public ProductResponse toResponse(Product p) {
        return toResponseStatic(p);
    }

    public static ProductResponse toResponseStatic(Product p) {

        ProductResponse r = new ProductResponse();

        r.setId(p.getId());
        r.setName(p.getName());
        r.setDescription(p.getDescription());
        r.setPrice(p.getPrice());
        r.setStateCode(p.getStateCode());
        r.setFashionType(p.getFashionType());
        r.setImageUrl(p.getImageUrl());
        r.setSellerName(p.getSellerName());
        r.setAvailableSizes(p.getAvailableSizes());
        r.setAvailableColors(p.getAvailableColors());
        r.setExtraImageUrls(p.getExtraImageUrls());
        r.setCategory(p.getCategory());
        r.setStock(p.getStock());

        return r;
    }
}