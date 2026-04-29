package com.trendaura.controller;

import com.trendaura.dto.CartItemResponse;
import com.trendaura.service.CartService;
import com.trendaura.util.CurrentUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<List<CartItemResponse>> getCart() {
        return ResponseEntity.ok(cartService.getCart(CurrentUser.id()));
    }

    @PostMapping("/items")
    public ResponseEntity<CartItemResponse> addItem(@RequestBody Map<String, Object> body) {
        Long productId = Long.valueOf(body.get("productId").toString());
        int quantity = body.containsKey("quantity") ? Integer.parseInt(body.get("quantity").toString()) : 1;
        return ResponseEntity.ok(cartService.addToCart(CurrentUser.id(), productId, quantity));
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<Void> updateQuantity(@PathVariable Long productId, @RequestBody Map<String, Integer> body) {
        int quantity = body.getOrDefault("quantity", 1);
        cartService.updateQuantity(CurrentUser.id(), productId, quantity);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long productId) {
        cartService.removeFromCart(CurrentUser.id(), productId);
        return ResponseEntity.ok().build();
    }
}
