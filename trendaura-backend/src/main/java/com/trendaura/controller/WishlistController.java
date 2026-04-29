package com.trendaura.controller;

import com.trendaura.dto.ProductResponse;
import com.trendaura.service.WishlistService;
import com.trendaura.util.CurrentUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getWishlist() {
        return ResponseEntity.ok(wishlistService.getWishlist(CurrentUser.id()));
    }

    @PostMapping("/items")
    public ResponseEntity<Void> addItem(@RequestBody Map<String, Long> body) {
        Long productId = body.get("productId");
        if (productId == null) return ResponseEntity.badRequest().build();
        wishlistService.addToWishlist(CurrentUser.id(), productId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long productId) {
        wishlistService.removeFromWishlist(CurrentUser.id(), productId);
        return ResponseEntity.ok().build();
    }
}
