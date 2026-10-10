package com.meridian.commerce.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * Partial update. Fields left null are NOT modified (see docs/BUSINESS_RULES.md §2.3).
 */
public record UpdateCustomerRequest(@Email String email,
                                    @Size(max = 80) String firstName,
                                    @Size(max = 80) String lastName,
                                    String phone,
                                    Boolean marketingOptIn) {
}
