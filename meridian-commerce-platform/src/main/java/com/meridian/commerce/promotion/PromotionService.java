package com.meridian.commerce.promotion;

import com.meridian.commerce.common.exception.BusinessRuleException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class PromotionService {

    private final PromotionRepository promotions;
    private final Clock clock;

    public PromotionService(PromotionRepository promotions, Clock clock) {
        this.promotions = promotions;
        this.clock = clock;
    }

    /**
     * Returns the promotion if it can be applied to a merchandise subtotal, otherwise throws.
     */
    @Transactional(readOnly = true)
    public Optional<Promotion> validate(String code, BigDecimal merchandiseSubtotal) {
        if (code == null || code.isBlank()) {
            return Optional.empty();
        }
        Promotion promo = promotions.findByCode(code.trim())
                .orElseThrow(() -> new BusinessRuleException("PROMO_INVALID", "Unknown promo code"));

        if (!promo.isActive()) {
            throw new BusinessRuleException("PROMO_INACTIVE", "Promo code is no longer active");
        }
        LocalDate today = LocalDate.now(clock);
        if (promo.getValidFrom() != null && today.isBefore(promo.getValidFrom())) {
            throw new BusinessRuleException("PROMO_NOT_STARTED", "Promo code is not active yet");
        }
        if (promo.getValidTo() != null && !today.isBefore(promo.getValidTo())) {
            throw new BusinessRuleException("PROMO_EXPIRED", "Promo code has expired");
        }
        if (promo.getMaxUses() != null && promo.getUsedCount() >= promo.getMaxUses()) {
            throw new BusinessRuleException("PROMO_EXHAUSTED", "Promo code usage limit reached");
        }
        if (promo.getMinOrderAmount() != null
                && merchandiseSubtotal.compareTo(promo.getMinOrderAmount()) <= 0
                && !merchandiseSubtotal.equals(promo.getMinOrderAmount())) {
            throw new BusinessRuleException("PROMO_MIN_NOT_MET",
                    "Order must be at least " + promo.getMinOrderAmount());
        }
        return Optional.of(promo);
    }

    @Transactional
    public void recordUse(Long promotionId) {
        Promotion p = promotions.findById(promotionId).orElseThrow();
        p.setUsedCount(p.getUsedCount() + 1);
    }

    @Transactional
    public int expireOldPromotions() {
        var expired = promotions.findExpired(LocalDate.now(clock));
        expired.forEach(p -> p.setActive(false));
        return expired.size();
    }
}
