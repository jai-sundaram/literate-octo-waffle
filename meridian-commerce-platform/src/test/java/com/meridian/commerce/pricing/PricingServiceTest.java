package com.meridian.commerce.pricing;

import com.meridian.commerce.catalog.TaxCategory;
import com.meridian.commerce.config.MeridianProperties;
import com.meridian.commerce.customer.Customer;
import com.meridian.commerce.customer.LoyaltyTier;
import com.meridian.commerce.promotion.Promotion;
import com.meridian.commerce.promotion.PromotionService;
import com.meridian.commerce.promotion.PromotionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PricingServiceTest {

    private PromotionService promotionService;
    private PricingService pricing;
    private Customer customer;

    @BeforeEach
    void setUp() {
        MeridianProperties props = new MeridianProperties();
        promotionService = mock(PromotionService.class);
        pricing = new PricingService(promotionService, new TaxService(props), new ShippingService(props));
        customer = new Customer();
        customer.setTier(LoyaltyTier.BRONZE);
    }

    @Test
    void noPromoNoLoyalty() {
        when(promotionService.validate(any(), any())).thenReturn(Optional.empty());
        var lines = List.of(new PriceLine("A", 2, new BigDecimal("50.00"), 500, TaxCategory.STANDARD));

        PriceBreakdown b = pricing.price(lines, customer, null, "TX");

        assertThat(b.subtotal()).isEqualByComparingTo("100.00");
        assertThat(b.shipping()).isEqualByComparingTo("0.00");
        assertThat(b.tax()).isEqualByComparingTo("6.25");
        assertThat(b.total()).isEqualByComparingTo("106.25");
    }

    @Test
    void taxIsCalculatedOnDiscountedMerchandise() {
        Promotion promo = new Promotion();
        promo.setType(PromotionType.FIXED);
        promo.setValue(new BigDecimal("20.00"));
        when(promotionService.validate(eq("TWENTY"), any())).thenReturn(Optional.of(promo));
        var lines = List.of(new PriceLine("A", 1, new BigDecimal("100.00"), 500, TaxCategory.STANDARD));

        PriceBreakdown b = pricing.price(lines, customer, "TWENTY", "TX");

        // 100 - 20 = 80 taxable; 80 * 6.25% = 5.00
        assertThat(b.promoDiscount()).isEqualByComparingTo("20.00");
        assertThat(b.tax()).isEqualByComparingTo("5.00");
    }

    @Test
    void percentPromo() {
        Promotion promo = new Promotion();
        promo.setType(PromotionType.PERCENT);
        promo.setValue(new BigDecimal("15"));
        when(promotionService.validate(eq("FALL15"), any())).thenReturn(Optional.of(promo));
        var lines = List.of(new PriceLine("A", 1, new BigDecimal("200.00"), 500, TaxCategory.EXEMPT));

        PriceBreakdown b = pricing.price(lines, customer, "FALL15", "OR");

        assertThat(b.promoDiscount()).isEqualByComparingTo("30.00");
    }
}
