package com.trendaura.service;

import com.trendaura.dto.ProductResponse;
import com.trendaura.entity.User;
import com.trendaura.entity.WishlistItem;
import com.trendaura.repository.ProductRepository;
import com.trendaura.repository.UserRepository;
import com.trendaura.repository.WishlistItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WishlistService {

    private final WishlistItemRepository wishlistItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ActivityTrackingService activityTrackingService;

    public List<ProductResponse> getWishlist(Long userId) {
        return wishlistItemRepository.findByUserIdOrderByAddedAtDesc(userId).stream()
                .map(wi -> ProductService.toResponseStatic(wi.getProduct()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void addToWishlist(Long userId, Long productId) {
        if (wishlistItemRepository.existsByUserIdAndProductId(userId, productId)) return;
        
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        WishlistItem wi = new WishlistItem();
        wi.setUser(user);
        wi.setProduct(productRepository.getReferenceById(productId));
        wishlistItemRepository.save(wi);
        
        // Log wishlist activity
        com.trendaura.dto.ActivityLogRequest activityRequest = com.trendaura.dto.ActivityLogRequest.builder()
                .pageName("Product Page")
                .actionName("ADD_TO_WISHLIST")
                .additionalData(String.format("{\"productId\":\"%d\"}", productId))
                .build();
        
        activityTrackingService.logActivity(activityRequest, user);
    }

    @Transactional
    public void removeFromWishlist(Long userId, Long productId) {
        wishlistItemRepository.findByUserIdAndProductId(userId, productId).ifPresent(item -> {
            wishlistItemRepository.delete(item);
            
            // Log wishlist activity
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                com.trendaura.dto.ActivityLogRequest activityRequest = com.trendaura.dto.ActivityLogRequest.builder()
                        .pageName("Wishlist Page")
                        .actionName("REMOVE_FROM_WISHLIST")
                        .additionalData(String.format("{\"productId\":\"%d\"}", productId))
                        .build();
                
                activityTrackingService.logActivity(activityRequest, user);
            }
        });
    }

    @Transactional
    public void clearWishlist(Long userId) {
        List<WishlistItem> items = wishlistItemRepository.findByUserIdOrderByAddedAtDesc(userId);
        wishlistItemRepository.deleteAll(items);
        
        // Log wishlist activity
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            com.trendaura.dto.ActivityLogRequest activityRequest = com.trendaura.dto.ActivityLogRequest.builder()
                    .pageName("Wishlist Page")
                    .actionName("CLEAR_WISHLIST")
                    .build();
            
            activityTrackingService.logActivity(activityRequest, user);
        }
    }

    public boolean isInWishlist(Long userId, Long productId) {
        return wishlistItemRepository.existsByUserIdAndProductId(userId, productId);
    }

    public int getWishlistItemCount(Long userId) {
        return wishlistItemRepository.findByUserIdOrderByAddedAtDesc(userId).size();
    }
}
