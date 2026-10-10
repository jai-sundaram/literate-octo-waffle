package com.meridian.commerce.reporting;

import java.math.BigDecimal;

public record ProductSalesRow(String sku, String productName, long unitsSold, BigDecimal revenue) {
}
