package com.meridian.commerce.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RefundRequest(@NotNull BigDecimal amount, @NotBlank String reason) {
}
