package com.meridian.commerce.payment.gateway;

import java.math.BigDecimal;

/** Abstraction over the external card processor (Stripe/Adyen in prod). */
public interface PaymentGatewayClient {

    GatewayResponse charge(GatewayChargeRequest request);

    GatewayResponse refund(String chargeReference, BigDecimal amount, String idempotencyKey);
}
