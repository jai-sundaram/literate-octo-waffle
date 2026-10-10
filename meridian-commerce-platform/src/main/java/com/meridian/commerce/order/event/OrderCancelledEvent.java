package com.meridian.commerce.order.event;

public record OrderCancelledEvent(Long orderId, String orderNumber, Long customerId, String reason) {
}
