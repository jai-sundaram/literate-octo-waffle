package com.meridian.commerce.catalog;

import com.meridian.commerce.audit.Audited;
import com.meridian.commerce.catalog.dto.ProductRequest;
import com.meridian.commerce.catalog.dto.ProductResponse;
import com.meridian.commerce.common.api.PageResponse;
import com.meridian.commerce.common.exception.BusinessRuleException;
import com.meridian.commerce.common.exception.ResourceNotFoundException;
import com.meridian.commerce.common.util.PageRequests;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository products;
    private final CategoryRepository categories;
    private final ProductMapper mapper;

    public ProductService(ProductRepository products, CategoryRepository categories, ProductMapper mapper) {
        this.products = products;
        this.categories = categories;
        this.mapper = mapper;
    }

    @Cacheable(value = "products", key = "#id")
    @Transactional(readOnly = true)
    public ProductResponse get(Long id) {
        return mapper.toResponse(products.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id)));
    }

    @Cacheable(value = "productsByCategory", key = "#slug")
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> byCategory(String slug, int page, int size) {
        return PageResponse.from(products.findByCategorySlug(slug, PageRequests.of(page, size, "name", "ASC"))
                .map(mapper::toResponse));
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> search(String q, int page, int size) {
        return PageResponse.from(products.search(q, PageRequests.of(page, size, "name", "ASC"))
                .map(mapper::toResponse));
    }

    @Transactional
    public ProductResponse create(ProductRequest req) {
        if (products.findBySku(req.sku()).isPresent()) {
            throw new BusinessRuleException("DUPLICATE_SKU", "SKU already exists: " + req.sku());
        }
        Product p = new Product();
        apply(p, req);
        return mapper.toResponse(products.save(p));
    }

    @Audited("PRODUCT_UPDATE")
    @CacheEvict(value = "product", key = "#id")
    @Transactional
    public ProductResponse update(Long id, ProductRequest req) {
        Product p = products.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", id));
        apply(p, req);
        return mapper.toResponse(p);
    }

    @CacheEvict(value = "products", key = "#id")
    @Transactional
    public void deactivate(Long id) {
        Product p = products.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", id));
        p.setActive(false);
    }

    private void apply(Product p, ProductRequest req) {
        p.setSku(req.sku());
        p.setName(req.name());
        p.setDescription(req.description());
        p.setPrice(req.price());
        p.setSalePrice(req.salePrice());
        p.setWeightGrams(req.weightGrams());
        p.setMaxPerOrder(req.maxPerOrder());
        if (req.taxCategory() != null) {
            p.setTaxCategory(req.taxCategory());
        }
        if (req.categorySlug() != null) {
            p.setCategory(categories.findBySlug(req.categorySlug()).orElse(null));
        }
    }
}
