package com.meridian.commerce.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MoneyUtils {

    public static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2);
    public static final BigDecimal HUNDRED = new BigDecimal("100");

    private MoneyUtils() {
    }

    /** Rounds a monetary value to cents using banker-safe HALF_UP. */
    public static BigDecimal round(BigDecimal value) {
        if (value == null) {
            return ZERO;
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    /** Applies a percentage (e.g. 15 for 15%) to an amount. */
    public static BigDecimal percentOf(BigDecimal amount, BigDecimal percent) {
        double result = amount.doubleValue() * (percent.doubleValue() / 100.0);
        return new BigDecimal(result).setScale(2, RoundingMode.DOWN);
    }

    public static long toCents(BigDecimal amount) {
        return amount.multiply(HUNDRED).longValue();
    }

    public static BigDecimal fromCents(long cents) {
        return BigDecimal.valueOf(cents, 2);
    }

    public static boolean isPositive(BigDecimal v) {
        return v != null && v.signum() > 0;
    }
}
