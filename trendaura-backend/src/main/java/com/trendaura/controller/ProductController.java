package com.trendaura.controller;

import com.trendaura.dto.ProductRequest;
import com.trendaura.dto.ProductResponse;
import com.trendaura.dto.ActivityLogRequest;
import com.trendaura.entity.User;
import com.trendaura.service.ActivityTrackingService;
import com.trendaura.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final ActivityTrackingService activityTrackingService;

    // =====================================================
    // ⭐ ADD PRODUCT
    // =====================================================
    @PostMapping
    public ResponseEntity<ProductResponse> addProduct(
            @Valid @RequestBody ProductRequest request,
            @AuthenticationPrincipal User user
    ) {
        ProductResponse response = productService.addProduct(request, user);
        if (user != null) {
            logActivity(user, "SELLER_PORTAL", "ADD_PRODUCT", request.getName());
        }
        return ResponseEntity.ok(response);
    }
    // =====================================================
    // ⭐ LIST PRODUCTS
    // =====================================================
    @GetMapping
    public ResponseEntity<List<ProductResponse>> listProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            Page<ProductResponse> productPage = productService.listAll(page, size);
            return ResponseEntity.ok(productPage.getContent());
        } catch (Exception e) {
            log.error("Error fetching products", e);
            // Return empty list instead of 500 error
            return ResponseEntity.ok(List.of());
        }
    }

    // =====================================================
    // ⭐ PRODUCT DETAILS
    // =====================================================
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                productService.getById(id)
        );
    }

    // =====================================================
    // ⭐ ADVANCED SEARCH
    // =====================================================
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(

            @RequestParam(required = false) String state,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {

        return ResponseEntity.ok(
                productService.advancedSearch(
                        state,
                        type,
                        category,
                        query,
                        page,
                        size
                )
        );
    }

    // =====================================================
    // ⭐ TRENDING PRODUCTS
    // =====================================================
    @GetMapping("/trending")
    public ResponseEntity<List<ProductResponse>> trending() {

        return ResponseEntity.ok(
                productService.getTrendingProducts()
        );
    }

    // =====================================================
    // ⭐ RECOMMENDATIONS
    // =====================================================
    @GetMapping("/recommended")
    public ResponseEntity<List<ProductResponse>> recommended(
            @AuthenticationPrincipal User user
    ) {

        if (user != null) {

            logActivity(
                    user,
                    "RECOMMENDED_PAGE",
                    "VIEW_RECOMMENDATIONS",
                    null
            );

            return ResponseEntity.ok(
                    productService.getRecommendedProducts(user)
            );
        }

        return ResponseEntity.ok(
                productService.getTrendingProducts()
        );
    }

    // =====================================================
    // ⭐ ACTIVITY LOGGER
    // =====================================================
    private void logActivity(
            User user,
            String page,
            String action,
            String data
    ) {

        if (user == null) return;

        ActivityLogRequest request = ActivityLogRequest.builder()
                .pageName(page)
                .actionName(action)
                .additionalData(data)
                .build();

        activityTrackingService.logActivity(request, user);
    }
}