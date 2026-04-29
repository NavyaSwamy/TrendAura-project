package com.trendaura.service;

import com.trendaura.dto.CartItemResponse;
import com.trendaura.entity.CartItem;
import com.trendaura.entity.Product;
import com.trendaura.entity.User;
import com.trendaura.repository.CartItemRepository;
import com.trendaura.repository.ProductRepository;
import com.trendaura.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ActivityTrackingService activityTrackingService;

    public List<CartItemResponse> getCart(Long userId) {
        List<CartItem> items = cartItemRepository.findByUserId(userId);
        return items.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public CartItemResponse addToCart(Long userId, Long productId, int quantity) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        CartItem item = cartItemRepository.findByUserIdAndProductId(userId, productId).orElse(null);
        
        if (item != null) {
            item.setQuantity(item.getQuantity() + quantity);
            item = cartItemRepository.save(item);
        } else {
            item = new CartItem();
            item.setUser(user);
            item.setProduct(product);
            item.setQuantity(quantity);
            item = cartItemRepository.save(item);
        }
        
        // Log cart activity
        com.trendaura.dto.ActivityLogRequest activityRequest = com.trendaura.dto.ActivityLogRequest.builder()
                .pageName("Product Page")
                .actionName("ADD_TO_CART")
                .additionalData(String.format("{\"productId\":\"%d\",\"quantity\":\"%d\"}", productId, quantity))
                .build();
        
        activityTrackingService.logActivity(activityRequest, user);
        
        return toResponse(item);
    }

    @Transactional
    public void updateQuantity(Long userId, Long productId, int quantity) {
        CartItem item = cartItemRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        if (quantity <= 0) {
            cartItemRepository.delete(item);
            return;
        }
        item.setQuantity(quantity);
        cartItemRepository.save(item);
    }

    @Transactional
    public void removeFromCart(Long userId, Long productId) {
        cartItemRepository.findByUserIdAndProductId(userId, productId)
                .ifPresent(cartItem -> {
                    cartItemRepository.delete(cartItem);
                    
                    // Log cart activity
                    User user = userRepository.findById(userId).orElse(null);
                    if (user != null) {
                        com.trendaura.dto.ActivityLogRequest activityRequest = com.trendaura.dto.ActivityLogRequest.builder()
                                .pageName("Cart Page")
                                .actionName("REMOVE_FROM_CART")
                                .additionalData(String.format("{\"productId\":\"%d\"}", productId))
                                .build();
                        
                        activityTrackingService.logActivity(activityRequest, user);
                    }
                });
    }

    @Transactional
    public void clearCart(Long userId) {
        List<CartItem> items = cartItemRepository.findByUserId(userId);
        cartItemRepository.deleteAll(items);
        
        // Log cart activity
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            com.trendaura.dto.ActivityLogRequest activityRequest = com.trendaura.dto.ActivityLogRequest.builder()
                    .pageName("Cart Page")
                    .actionName("CLEAR_CART")
                    .build();
            
            activityTrackingService.logActivity(activityRequest, user);
        }
    }

    public BigDecimal getCartTotal(Long userId) {
        List<CartItem> items = cartItemRepository.findByUserId(userId);
        return items.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getCartItemCount(Long userId) {
        List<CartItem> items = cartItemRepository.findByUserId(userId);
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }

    private CartItemResponse toResponse(CartItem c) {
        Product p = c.getProduct();
        CartItemResponse r = new CartItemResponse();
        r.setCartItemId(c.getId());
        r.setProductId(p.getId());
        r.setName(p.getName());
        r.setImageUrl(p.getImageUrl());
        r.setPrice(p.getPrice());
        r.setQuantity(c.getQuantity());
        r.setLineTotal(p.getPrice().multiply(BigDecimal.valueOf(c.getQuantity())));
        return r;
    }
}
