package com.meridian.commerce.customer;

import com.meridian.commerce.config.MeridianProperties;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class LoyaltyServiceTest {

    private final LoyaltyService loyalty = new LoyaltyService(new MeridianProperties(), mock(CustomerRepository.class));

    @Test
    void tiers() {
        assertThat(loyalty.tierFor(0)).isEqualTo(LoyaltyTier.BRONZE);
        assertThat(loyalty.tierFor(1500)).isEqualTo(LoyaltyTier.SILVER);
        assertThat(loyalty.tierFor(12000)).isEqualTo(LoyaltyTier.GOLD);
        assertThat(loyalty.tierFor(50000)).isEqualTo(LoyaltyTier.PLATINUM);
    }

    @Test
    void pointsPerDollar() {
        assertThat(loyalty.pointsFor(new BigDecimal("25.00"))).isEqualTo(250);
    }
}
