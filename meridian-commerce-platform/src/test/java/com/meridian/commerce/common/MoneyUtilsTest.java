package com.meridian.commerce.common;

import com.meridian.commerce.common.util.MoneyUtils;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class MoneyUtilsTest {

    @Test
    void percentOfRoundNumbers() {
        assertThat(MoneyUtils.percentOf(new BigDecimal("200.00"), new BigDecimal("15"))).isEqualByComparingTo("30.00");
        assertThat(MoneyUtils.percentOf(new BigDecimal("80.00"), new BigDecimal("25"))).isEqualByComparingTo("20.00");
    }

    @Test
    void centsRoundTrip() {
        assertThat(MoneyUtils.fromCents(MoneyUtils.toCents(new BigDecimal("12.34")))).isEqualByComparingTo("12.34");
    }
}
