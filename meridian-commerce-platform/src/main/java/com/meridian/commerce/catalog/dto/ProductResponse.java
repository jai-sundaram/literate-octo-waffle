package com.meridian.commerce.catalog.dto;

import com.meridian.commerce.catalog.TaxCategory;

import java.math.BigDecimal;

public record ProductResponse(Long id, String sku, String name, String description, BigDecimal price,
                              BigDecimal salePrice, BigDecimal effectivePrice, int weightGrams,
                              String categorySlug, TaxCategory taxCategory, boolean active) {
}
