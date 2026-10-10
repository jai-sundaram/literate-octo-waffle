package com.meridian.commerce.promotion;

public enum PromotionType {
    /** value is a percentage, e.g. 15 = 15% off the merchandise subtotal */
    PERCENT,
    /** value is a fixed currency amount off the merchandise subtotal */
    FIXED,
    /** waives shipping; value ignored */
    FREE_SHIPPING
}
