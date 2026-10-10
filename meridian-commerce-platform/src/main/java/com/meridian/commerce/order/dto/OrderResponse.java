package com.meridian.commerce.order.dto;

import com.meridian.commerce.order.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(Long id, String orderNumber, Long customerId, OrderStatus status,
                            List<OrderLineResponse> lines, BigDecimal subtotal, BigDecimal promoDiscount,
                            BigDecimal loyaltyDiscount, BigDecimal shipping, BigDecimal tax, BigDecimal total,
                            String currency, Instant placedAt, String trackingNumber) {
}
