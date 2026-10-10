package com.meridian.commerce.order.event;

public record OrderShippedEvent(Long orderId, String orderNumber, Long customerId, String trackingNumber) {
}
