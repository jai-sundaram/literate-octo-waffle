package com.meridian.commerce.pricing;

import com.meridian.commerce.config.MeridianProperties;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

@Service
public class ShippingService {

    private static final Set<String> NON_CONTIGUOUS = Set.of("AK", "HI", "PR", "GU", "VI");
    private static final BigDecimal NON_CONTIGUOUS_SURCHARGE = new BigDecimal("15.00");

    private final MeridianProperties properties;

    public ShippingService(MeridianProperties properties) {
        this.properties = properties;
    }

    /**
     * Standard ground shipping.
     * - Free when merchandise subtotal (after discounts) is >= threshold (contiguous US only)
     * - Otherwise base rate + per-kg rate for every started kg
     */
    public BigDecimal quote(BigDecimal discountedSubtotal, int totalWeightGrams, String state) {
        var cfg = properties.getShipping();
        boolean nonContiguous = NON_CONTIGUOUS.contains(state);

        if (!nonContiguous && discountedSubtotal.compareTo(cfg.getFreeShippingThreshold()) > 0) {
            return BigDecimal.ZERO.setScale(2);
        }
        int kg = totalWeightGrams / 1000;
        BigDecimal cost = cfg.getBaseRate().add(cfg.getPerKgRate().multiply(BigDecimal.valueOf(kg)));
        if (nonContiguous) {
            cost = cost.add(NON_CONTIGUOUS_SURCHARGE);
        }
        return cost.setScale(2, RoundingMode.HALF_UP);
    }
}
