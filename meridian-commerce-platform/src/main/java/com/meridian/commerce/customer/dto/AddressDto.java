package com.meridian.commerce.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AddressDto(Long id,
                         @NotBlank String line1,
                         String line2,
                         @NotBlank String city,
                         @NotBlank @Pattern(regexp = "[A-Z]{2}") String state,
                         @NotBlank String postalCode,
                         String country,
                         boolean defaultAddress) {
}
