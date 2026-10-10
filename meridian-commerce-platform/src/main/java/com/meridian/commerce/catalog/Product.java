package com.meridian.commerce.catalog;

import com.meridian.commerce.common.domain.AuditableEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(length = 4000)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "sale_price", precision = 10, scale = 2)
    private BigDecimal salePrice;

    @Column(name = "weight_grams")
    private int weightGrams;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "tax_category")
    private TaxCategory taxCategory = TaxCategory.STANDARD;

    private boolean active = true;

    @Column(name = "max_per_order")
    private Integer maxPerOrder;

    public BigDecimal effectivePrice() {
        if (salePrice != null && salePrice.compareTo(price) < 0) {
            return salePrice;
        }
        return price;
    }

    public Long getId() { return id; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getSalePrice() { return salePrice; }
    public void setSalePrice(BigDecimal salePrice) { this.salePrice = salePrice; }
    public int getWeightGrams() { return weightGrams; }
    public void setWeightGrams(int weightGrams) { this.weightGrams = weightGrams; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public TaxCategory getTaxCategory() { return taxCategory; }
    public void setTaxCategory(TaxCategory taxCategory) { this.taxCategory = taxCategory; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Integer getMaxPerOrder() { return maxPerOrder; }
    public void setMaxPerOrder(Integer maxPerOrder) { this.maxPerOrder = maxPerOrder; }
}
