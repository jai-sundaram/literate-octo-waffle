package com.meridian.commerce.pricing;

import java.math.BigDecimal;

public record PriceBreakdown(BigDecimal subtotal,
                             BigDecimal promoDiscount,
                             BigDecimal loyaltyDiscount,
                             BigDecimal shipping,
                             BigDecimal tax,
                             BigDecimal total,
                             Long appliedPromotionId) {
}
