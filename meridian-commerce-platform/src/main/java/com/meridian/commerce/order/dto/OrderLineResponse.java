package com.meridian.commerce.order.dto;

import java.math.BigDecimal;

public record OrderLineResponse(String sku, String productName, int quantity, BigDecimal unitPrice, BigDecimal lineTotal) {
}
