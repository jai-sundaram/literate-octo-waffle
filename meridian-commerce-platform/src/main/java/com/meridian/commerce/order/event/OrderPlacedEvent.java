package com.meridian.commerce.order.event;

public record OrderPlacedEvent(Long orderId, String orderNumber, Long customerId) {
}
