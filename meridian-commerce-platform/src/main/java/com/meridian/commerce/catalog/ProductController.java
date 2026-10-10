package com.meridian.commerce.catalog;

import com.meridian.commerce.catalog.dto.ProductRequest;
import com.meridian.commerce.catalog.dto.ProductResponse;
import com.meridian.commerce.common.api.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/api/products/{id}")
    public ProductResponse get(@PathVariable Long id) {
        return productService.get(id);
    }

    @GetMapping("/api/products")
    public PageResponse<ProductResponse> search(@RequestParam(defaultValue = "") String q,
                                                @RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "24") int size) {
        return productService.search(q, page, size);
    }

    @GetMapping("/api/categories/{slug}/products")
    public PageResponse<ProductResponse> byCategory(@PathVariable String slug,
                                                    @RequestParam(defaultValue = "1") int page,
                                                    @RequestParam(defaultValue = "24") int size) {
        return productService.byCategory(slug, page, size);
    }

    @PostMapping("/api/admin/products")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@Valid @RequestBody ProductRequest request) {
        return productService.create(request);
    }

    @PutMapping("/api/admin/products/{id}")
    public ProductResponse update(@PathVariable Long id, @RequestBody ProductRequest request) {
        return productService.update(id, request);
    }

    @DeleteMapping("/api/admin/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Long id) {
        productService.deactivate(id);
    }
}
