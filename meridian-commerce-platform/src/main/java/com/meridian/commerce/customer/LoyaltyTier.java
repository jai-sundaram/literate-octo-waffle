package com.meridian.commerce.customer;

import java.math.BigDecimal;

public enum LoyaltyTier {
    BRONZE(new BigDecimal("0.00")),
    SILVER(new BigDecimal("0.02")),
    GOLD(new BigDecimal("0.05")),
    PLATINUM(new BigDecimal("0.08"));

    private final BigDecimal discountRate;

    LoyaltyTier(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    public BigDecimal getDiscountRate() {
        return discountRate;
    }
}
