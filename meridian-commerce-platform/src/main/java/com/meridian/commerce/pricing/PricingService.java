package com.meridian.commerce.pricing;

import com.meridian.commerce.common.util.MoneyUtils;
import com.meridian.commerce.customer.Customer;
import com.meridian.commerce.promotion.Promotion;
import com.meridian.commerce.promotion.PromotionService;
import com.meridian.commerce.promotion.PromotionType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

/**
 * Order pricing pipeline. See docs/BUSINESS_RULES.md §4 for the canonical order of operations:
 *   1. merchandise subtotal
 *   2. promo discount
 *   3. loyalty tier discount (only if promo is stackable or no promo)
 *   4. shipping on discounted subtotal
 *   5. tax on discounted merchandise (shipping not taxed)
 */
@Service
public class PricingService {

    private final PromotionService promotionService;
    private final TaxService taxService;
    private final ShippingService shippingService;

    public PricingService(PromotionService promotionService, TaxService taxService, ShippingService shippingService) {
        this.promotionService = promotionService;
        this.taxService = taxService;
        this.shippingService = shippingService;
    }

    public PriceBreakdown price(List<PriceLine> lines, Customer customer, String promoCode, String shipState) {
        BigDecimal subtotal = lines.stream().map(PriceLine::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        Optional<Promotion> promo = promotionService.validate(promoCode, subtotal);
        BigDecimal promoDiscount = promo.map(p -> discountFor(p, subtotal)).orElse(BigDecimal.ZERO);

        BigDecimal loyaltyDiscount = BigDecimal.ZERO;
        if (promo.isEmpty() || promo.get().isStackableWithLoyalty()) {
            loyaltyDiscount = subtotal.multiply(customer.getTier().getDiscountRate());
        }

        BigDecimal discounted = subtotal.subtract(promoDiscount).subtract(loyaltyDiscount);
        if (discounted.signum() < 0) {
            discounted = BigDecimal.ZERO;
        }

        int weight = lines.stream().mapToInt(l -> l.unitWeightGrams() * l.quantity()).sum();
        BigDecimal shipping = promo.isPresent() && promo.get().getType() == PromotionType.FREE_SHIPPING
                ? BigDecimal.ZERO
                : shippingService.quote(discounted, weight, shipState);

        BigDecimal tax = BigDecimal.ZERO;
        if (!customer.isTaxExempt()) {
            for (PriceLine line : lines) {
                tax = tax.add(taxService.taxFor(line.lineTotal(), line.taxCategory(), shipState));
            }
        }

        BigDecimal total = discounted.add(shipping).add(tax);
        return new PriceBreakdown(
                MoneyUtils.round(subtotal),
                MoneyUtils.round(promoDiscount),
                MoneyUtils.round(loyaltyDiscount),
                MoneyUtils.round(shipping),
                MoneyUtils.round(tax),
                MoneyUtils.round(total),
                promo.map(Promotion::getId).orElse(null));
    }

    private BigDecimal discountFor(Promotion promo, BigDecimal subtotal) {
        return switch (promo.getType()) {
            case PERCENT -> MoneyUtils.percentOf(subtotal, promo.getValue());
            case FIXED -> promo.getValue().setScale(2, RoundingMode.HALF_UP);
            case FREE_SHIPPING -> BigDecimal.ZERO;
        };
    }
}
