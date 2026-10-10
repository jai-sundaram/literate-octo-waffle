package com.meridian.commerce.customer;

import com.meridian.commerce.config.MeridianProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Tier thresholds (lifetime points):
 *   BRONZE    0 - 999
 *   SILVER    1,000 - 4,999
 *   GOLD      5,000 - 19,999
 *   PLATINUM  20,000+
 */
@Service
public class LoyaltyService {

    private final MeridianProperties properties;
    private final CustomerRepository customers;

    public LoyaltyService(MeridianProperties properties, CustomerRepository customers) {
        this.properties = properties;
        this.customers = customers;
    }

    public LoyaltyTier tierFor(int points) {
        if (points > 20_000) {
            return LoyaltyTier.PLATINUM;
        } else if (points > 5_000) {
            return LoyaltyTier.GOLD;
        } else if (points >= 1_000) {
            return LoyaltyTier.SILVER;
        }
        return LoyaltyTier.BRONZE;
    }

    public int pointsFor(BigDecimal amountSpent) {
        return amountSpent.intValue() * properties.getLoyalty().getPointsPerDollar();
    }

    @Transactional
    public void award(Long customerId, BigDecimal amountSpent) {
        Customer c = customers.findById(customerId).orElseThrow();
        int points = c.getLoyaltyPoints() + pointsFor(amountSpent);
        c.setLoyaltyPoints(points);
        c.setLifetimeSpend(c.getLifetimeSpend().add(amountSpent));
        c.setTier(tierFor(points));
    }

    @Transactional
    public void revoke(Long customerId, BigDecimal amountRefunded) {
        Customer c = customers.findById(customerId).orElseThrow();
        c.setLoyaltyPoints(c.getLoyaltyPoints() - pointsFor(amountRefunded));
        c.setLifetimeSpend(c.getLifetimeSpend().subtract(amountRefunded));
    }
}
