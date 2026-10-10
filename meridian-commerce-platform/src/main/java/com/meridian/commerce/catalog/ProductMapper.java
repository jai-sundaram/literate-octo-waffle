package com.meridian.commerce.catalog;

import com.meridian.commerce.catalog.dto.ProductResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product p) {
        return new ProductResponse(p.getId(), p.getSku(), p.getName(), p.getDescription(), p.getPrice(),
                p.getSalePrice(), p.effectivePrice(), p.getWeightGrams(),
                p.getCategory() == null ? null : p.getCategory().getSlug(),
                p.getTaxCategory(), p.isActive());
    }
}
