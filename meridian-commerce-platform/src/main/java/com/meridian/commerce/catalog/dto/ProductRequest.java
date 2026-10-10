package com.meridian.commerce.catalog.dto;

import com.meridian.commerce.catalog.TaxCategory;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductRequest(@NotBlank @Pattern(regexp = "[A-Z0-9-]{4,32}") String sku,
                             @NotBlank String name,
                             String description,
                             @NotNull @DecimalMin("0.01") BigDecimal price,
                             BigDecimal salePrice,
                             @PositiveOrZero int weightGrams,
                             String categorySlug,
                             TaxCategory taxCategory,
                             Integer maxPerOrder) {
}
