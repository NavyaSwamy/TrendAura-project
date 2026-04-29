package com.trendaura.controller;

import com.trendaura.dto.ProductResponse;
import com.trendaura.service.ProductService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final ProductService productService;

    // =====================================================
    // ⭐ GET RECOMMENDATIONS
    // =====================================================
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getRecommendations(
            @RequestParam(defaultValue = "10") int limit
    ) {

        List<ProductResponse> list;

        try {

            // ⭐ Real production systems should use:
            // - User behaviour history
            // - Collaborative filtering
            // - ML ranking models

            list = productService.advancedSearch(
                    null,
                    null,
                    null,
                    null,
                    0,
                    limit
            );

        } catch (Exception e) {

            // Fallback → Trending products
            list = productService.advancedSearch(
                    null,
                    null,
                    null,
                    null,
                    0,
                    limit
            );
        }

        return ResponseEntity.ok(list);
    }
}