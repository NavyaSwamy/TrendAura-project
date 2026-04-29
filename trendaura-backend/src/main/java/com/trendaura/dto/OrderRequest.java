package com.trendaura.dto;

import jakarta.validation.constraints.NotNull;

public class OrderRequest {

    @NotNull
    private Long addressId;

    /** Optional: shipping amount override (e.g. 50 INR). */
    private java.math.BigDecimal shippingAmount;

    public Long getAddressId() { return addressId; }
    public void setAddressId(Long addressId) { this.addressId = addressId; }
    public java.math.BigDecimal getShippingAmount() { return shippingAmount; }
    public void setShippingAmount(java.math.BigDecimal shippingAmount) { this.shippingAmount = shippingAmount; }
}
