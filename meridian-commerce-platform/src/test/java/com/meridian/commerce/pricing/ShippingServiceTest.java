package com.meridian.commerce.pricing;

import com.meridian.commerce.config.MeridianProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ShippingServiceTest {

    private ShippingService shipping;

    @BeforeEach
    void setUp() {
        shipping = new ShippingService(new MeridianProperties());
    }

    @Test
    void freeShippingAboveThreshold() {
        assertThat(shipping.quote(new BigDecimal("120.00"), 3000, "VA")).isEqualByComparingTo("0.00");
    }

    @Test
    void freeShippingAtExactlyThreshold() {
        assertThat(shipping.quote(new BigDecimal("75.00"), 500, "VA")).isEqualByComparingTo("0.00");
    }

    @Test
    void chargesBasePlusWeight() {
        // 2 kg -> 5.99 + 2 * 1.25
        assertThat(shipping.quote(new BigDecimal("20.00"), 2000, "TX")).isEqualByComparingTo("8.49");
    }

    @Test
    void nonContiguousNeverFree() {
        assertThat(shipping.quote(new BigDecimal("500.00"), 1000, "HI")).isEqualByComparingTo("22.24");
    }
}
