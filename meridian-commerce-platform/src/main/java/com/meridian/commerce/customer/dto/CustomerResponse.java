package com.meridian.commerce.customer.dto;

import com.meridian.commerce.customer.LoyaltyTier;

import java.math.BigDecimal;
import java.util.List;

public record CustomerResponse(Long id, String email, String firstName, String lastName, String phone,
                               LoyaltyTier tier, int loyaltyPoints, BigDecimal lifetimeSpend,
                               Boolean marketingOptIn, List<AddressDto> addresses) {
}
