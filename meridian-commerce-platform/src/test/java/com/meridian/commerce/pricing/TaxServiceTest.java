package com.meridian.commerce.pricing;

import com.meridian.commerce.catalog.TaxCategory;
import com.meridian.commerce.config.MeridianProperties;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class TaxServiceTest {

    private final TaxService tax = new TaxService(new MeridianProperties());

    @Test
    void standardRateCalifornia() {
        assertThat(tax.taxFor(new BigDecimal("100.00"), TaxCategory.STANDARD, "CA")).isEqualByComparingTo("8.25");
    }

    @Test
    void exemptCategoryIsZero() {
        assertThat(tax.taxFor(new BigDecimal("100.00"), TaxCategory.EXEMPT, "NY")).isEqualByComparingTo("0");
    }

    @Test
    void unknownStateUsesDefault() {
        assertThat(tax.rateFor("ZZ")).isEqualByComparingTo("0.06");
    }
}
