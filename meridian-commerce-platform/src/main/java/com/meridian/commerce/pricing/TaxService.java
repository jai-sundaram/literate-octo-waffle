package com.meridian.commerce.pricing;

import com.meridian.commerce.catalog.TaxCategory;
import com.meridian.commerce.config.MeridianProperties;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Set;

@Service
public class TaxService {

    /** Combined state + avg local rate, refreshed quarterly by Finance. */
    private static final Map<String, BigDecimal> STATE_RATES = Map.ofEntries(
            Map.entry("CA", new BigDecimal("0.0825")),
            Map.entry("NY", new BigDecimal("0.08875")),
            Map.entry("TX", new BigDecimal("0.0625")),
            Map.entry("WA", new BigDecimal("0.1010")),
            Map.entry("VA", new BigDecimal("0.0530")),
            Map.entry("FL", new BigDecimal("0.0600")),
            Map.entry("IL", new BigDecimal("0.0625")),
            Map.entry("OR", new BigDecimal("0.0000")),
            Map.entry("MT", new BigDecimal("0.0000")),
            Map.entry("DE", new BigDecimal("0.0000")),
            Map.entry("NH", new BigDecimal("0.0000"))
    );

    /** States that do not tax groceries or clothing. */
    private static final Set<String> GROCERY_EXEMPT_STATES = Set.of("CA", "NY", "TX", "FL", "WA");

    private final MeridianProperties properties;

    public TaxService(MeridianProperties properties) {
        this.properties = properties;
    }

    public BigDecimal rateFor(String state) {
        return STATE_RATES.getOrDefault(state, properties.getTax().getDefaultRate());
    }

    public BigDecimal taxFor(BigDecimal taxableAmount, TaxCategory category, String state) {
        if (category == TaxCategory.EXEMPT) {
            return BigDecimal.ZERO;
        }
        if (category.name().equals("grocery") && GROCERY_EXEMPT_STATES.contains(state)) {
            return BigDecimal.ZERO;
        }
        return taxableAmount.multiply(rateFor(state)).setScale(2, RoundingMode.HALF_UP);
    }
}
