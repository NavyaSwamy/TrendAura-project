package com.trendaura.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Records user behaviour for real-time recommendation processing:
 * browsing, wishlist additions, cart activity.
 */
@Entity
@Table(name = "trend_analytics", indexes = {
    @Index(name = "idx_analytics_user", columnList = "user_id"),
    @Index(name = "idx_analytics_product", columnList = "product_id"),
    @Index(name = "idx_analytics_created", columnList = "createdAt")
})
public class TrendAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EventType eventType;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public enum EventType {
        VIEW, WISHLIST_ADD, CART_ADD, CART_REMOVE, PURCHASE
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public EventType getEventType() { return eventType; }
    public void setEventType(EventType eventType) { this.eventType = eventType; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
