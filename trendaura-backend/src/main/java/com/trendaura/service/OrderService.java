package com.trendaura.service;

import com.trendaura.dto.*;
import com.trendaura.entity.*;
import com.trendaura.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final AddressRepository addressRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository,
                        CartItemRepository cartItemRepository, AddressRepository addressRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.cartItemRepository = cartItemRepository;
        this.addressRepository = addressRepository;
    }

    @Transactional
    public OrderResponse createOrder(Long userId, OrderRequest req) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        if (cartItems.isEmpty()) throw new RuntimeException("Cart is empty");

        Address addr = addressRepository.findById(req.getAddressId()).orElseThrow(() -> new RuntimeException("Address not found"));
        if (!addr.getUser().getId().equals(userId)) throw new RuntimeException("Invalid address");

        BigDecimal subtotal = BigDecimal.ZERO;
        Order order = new Order();
        order.setUser(user);
        order.setOrderNumber("ORD-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
        order.setStatus(Order.OrderStatus.PROCESSING);
        order.setShippingAddressJson(toAddressJson(addr));
        order.setShippingAmount(req.getShippingAmount() != null ? req.getShippingAmount() : new BigDecimal("50"));
        order = orderRepository.save(order);

        for (CartItem ci : cartItems) {
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(ci.getProduct());
            oi.setQuantity(ci.getQuantity());
            oi.setUnitPrice(ci.getProduct().getPrice());
            oi.setProductName(ci.getProduct().getName());
            oi.setProductImageUrl(ci.getProduct().getImageUrl());
            order.getItems().add(oi);
            subtotal = subtotal.add(ci.getProduct().getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())));
        }
        order.setSubtotal(subtotal);
        order.setTotal(subtotal.add(order.getShippingAmount()));
        order = orderRepository.save(order);

        cartItemRepository.deleteAllByUserId(userId);

        return toOrderResponse(order);
    }

    public List<OrderResponse> getOrdersByUser(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toOrderResponse).collect(Collectors.toList());
    }

    public OrderResponse getOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        if (!order.getUser().getId().equals(userId)) throw new RuntimeException("Forbidden");
        return toOrderResponse(order);
    }

    public OrderResponse getOrderByNumber(Long userId, String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber).orElseThrow(() -> new RuntimeException("Order not found"));
        if (!order.getUser().getId().equals(userId)) throw new RuntimeException("Forbidden");
        return toOrderResponse(order);
    }

    private String toAddressJson(Address a) {
        return String.format("{\"line1\":\"%s\",\"line2\":\"%s\",\"city\":\"%s\",\"state\":\"%s\",\"pincode\":\"%s\"}",
                escape(a.getLine1()), escape(a.getLine2()), escape(a.getCity()), escape(a.getState()), escape(a.getPincode()));
    }

    private String escape(String s) { return s == null ? "" : s.replace("\"", "\\\""); }

    private OrderResponse toOrderResponse(Order o) {
        OrderResponse r = new OrderResponse();
        r.setId(o.getId());
        r.setOrderNumber(o.getOrderNumber());
        r.setStatus(o.getStatus().name());
        r.setSubtotal(o.getSubtotal());
        r.setShippingAmount(o.getShippingAmount());
        r.setTotal(o.getTotal());
        r.setShippingAddressJson(o.getShippingAddressJson());
        r.setCreatedAt(o.getCreatedAt());
        r.setItems(o.getItems().stream().map(this::toItemResponse).collect(Collectors.toList()));
        return r;
    }

    private OrderItemResponse toItemResponse(OrderItem oi) {
        OrderItemResponse r = new OrderItemResponse();
        r.setProductId(oi.getProduct().getId());
        r.setProductName(oi.getProductName());
        r.setProductImageUrl(oi.getProductImageUrl());
        r.setQuantity(oi.getQuantity());
        r.setUnitPrice(oi.getUnitPrice());
        r.setLineTotal(oi.getUnitPrice().multiply(BigDecimal.valueOf(oi.getQuantity())));
        return r;
    }
}
