package com.meridian.commerce.pricing;

import com.meridian.commerce.catalog.TaxCategory;

import java.math.BigDecimal;

public record PriceLine(String sku, int quantity, BigDecimal unitPrice, int unitWeightGrams, TaxCategory taxCategory) {

    public BigDecimal lineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
