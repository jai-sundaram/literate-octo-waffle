package com.meridian.commerce.pricing;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * Static FX table. Rates are expressed as "units of foreign currency per 1 USD".
 */
@Service
public class CurrencyService {

    private static final Map<String, BigDecimal> USD_TO = Map.of(
            "USD", BigDecimal.ONE,
            "CAD", new BigDecimal("1.36"),
            "EUR", new BigDecimal("0.92"),
            "GBP", new BigDecimal("0.79"),
            "MXN", new BigDecimal("17.10")
    );

    public boolean isSupported(String currency) {
        return USD_TO.containsKey(currency);
    }

    /** Converts an amount in USD into the target currency. */
    public BigDecimal fromUsd(BigDecimal usdAmount, String currency) {
        BigDecimal rate = USD_TO.get(currency);
        return usdAmount.divide(rate, 2, RoundingMode.HALF_UP);
    }

    /** Converts an amount in the given currency back to USD. */
    public BigDecimal toUsd(BigDecimal amount, String currency) {
        BigDecimal rate = USD_TO.get(currency);
        return amount.divide(rate, 2, RoundingMode.HALF_UP);
    }
}
