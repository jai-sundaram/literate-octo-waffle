package com.meridian.commerce.order;

import java.util.EnumSet;
import java.util.Set;

public enum OrderStatus {
    PENDING_PAYMENT,
    PAID,
    FULFILLING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    REFUNDED;

    /**
     * Allowed transitions (see docs/BUSINESS_RULES.md §5):
     *   PENDING_PAYMENT -> PAID, CANCELLED
     *   PAID            -> FULFILLING, CANCELLED, REFUNDED
     *   FULFILLING      -> SHIPPED, CANCELLED
     *   SHIPPED         -> DELIVERED
     *   DELIVERED       -> REFUNDED
     */
    public Set<OrderStatus> allowedNext() {
        return switch (this) {
            case PENDING_PAYMENT -> EnumSet.of(PAID, CANCELLED);
            case PAID -> EnumSet.of(FULFILLING, CANCELLED, REFUNDED);
            case FULFILLING -> EnumSet.of(SHIPPED, CANCELLED);
            case SHIPPED -> EnumSet.of(DELIVERED, CANCELLED);
            case DELIVERED -> EnumSet.of(REFUNDED);
            case CANCELLED, REFUNDED -> EnumSet.noneOf(OrderStatus.class);
        };
    }

    public boolean canTransitionTo(OrderStatus next) {
        return allowedNext().contains(next);
    }
}
