package com.meridian.commerce.promotion;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "promotions")
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Stored upper-case. */
    @Column(nullable = false, unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    private PromotionType type;

    @Column(name = "discount_value", precision = 10, scale = 2)
    private BigDecimal value;

    @Column(name = "min_order_amount", precision = 10, scale = 2)
    private BigDecimal minOrderAmount;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    /** Inclusive. */
    @Column(name = "valid_to")
    private LocalDate validTo;

    @Column(name = "max_uses")
    private Integer maxUses;

    @Column(name = "used_count")
    private int usedCount;

    @Column(name = "stackable_with_loyalty")
    private boolean stackableWithLoyalty;

    private boolean active = true;

    public Long getId() { return id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public PromotionType getType() { return type; }
    public void setType(PromotionType type) { this.type = type; }
    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }
    public BigDecimal getMinOrderAmount() { return minOrderAmount; }
    public void setMinOrderAmount(BigDecimal minOrderAmount) { this.minOrderAmount = minOrderAmount; }
    public LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }
    public LocalDate getValidTo() { return validTo; }
    public void setValidTo(LocalDate validTo) { this.validTo = validTo; }
    public Integer getMaxUses() { return maxUses; }
    public void setMaxUses(Integer maxUses) { this.maxUses = maxUses; }
    public int getUsedCount() { return usedCount; }
    public void setUsedCount(int usedCount) { this.usedCount = usedCount; }
    public boolean isStackableWithLoyalty() { return stackableWithLoyalty; }
    public void setStackableWithLoyalty(boolean stackableWithLoyalty) { this.stackableWithLoyalty = stackableWithLoyalty; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
