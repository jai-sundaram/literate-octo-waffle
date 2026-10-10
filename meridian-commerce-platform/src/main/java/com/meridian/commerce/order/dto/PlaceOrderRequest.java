package com.meridian.commerce.order.dto;

import jakarta.validation.constraints.NotNull;

public record PlaceOrderRequest(@NotNull Long shippingAddressId, String currency, String paymentToken) {
}
