package com.meridian.commerce.payment.gateway;

import java.math.BigDecimal;

public record GatewayChargeRequest(String paymentToken, BigDecimal amount, String currency, String idempotencyKey) {
}
