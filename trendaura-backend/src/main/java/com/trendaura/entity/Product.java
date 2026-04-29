package com.trendaura.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Outfit/Product - state-wise fashion item with metadata.
 * Aligns with: state-wise fashion datasets, outfit images and metadata.
 */
@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_product_state", columnList = "stateCode"),
    @Index(name = "idx_product_fashion_type", columnList = "fashionType"),
    @Index(name = "idx_product_state_type", columnList = "stateCode, fashionType")
})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 2000)
    private String description;

    @NotNull
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    /** State code matching frontend (e.g. andhra_pradesh, tamil_nadu). */
    @NotBlank
    @Column(nullable = false, length = 80)
    private String stateCode;

    /** Fashion type: saree, salwar_kameez, lehenga, dhoti. */
    @NotBlank
    @Column(nullable = false, length = 50)
    private String fashionType;

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 500)
    private String imageUrl2;

    @Column(length = 150)
    private String sellerName;

    @Column(length = 1000)
    private String availableSizes;

    @Column(length = 1000)
    private String availableColors;

    @Column(length = 2000)
    private String extraImageUrls;

    @Column(length = 100)
    private String category;

    private boolean active = true;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    private Instant updatedAt = Instant.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getStateCode() { return stateCode; }
    public void setStateCode(String stateCode) { this.stateCode = stateCode; }
    public String getFashionType() { return fashionType; }
    public void setFashionType(String fashionType) { this.fashionType = fashionType; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getImageUrl2() { return imageUrl2; }
    public void setImageUrl2(String imageUrl2) { this.imageUrl2 = imageUrl2; }
    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }
    public String getAvailableSizes() { return availableSizes; }
    public void setAvailableSizes(String availableSizes) { this.availableSizes = availableSizes; }
    public String getAvailableColors() { return availableColors; }
    public void setAvailableColors(String availableColors) { this.availableColors = availableColors; }
    public String getExtraImageUrls() { return extraImageUrls; }
    public void setExtraImageUrls(String extraImageUrls) { this.extraImageUrls = extraImageUrls; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    private Integer stock;

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
