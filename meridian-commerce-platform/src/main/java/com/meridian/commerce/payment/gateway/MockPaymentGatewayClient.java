package com.meridian.commerce.payment.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * In-memory stand-in for the card processor used in local/dev. Behaves like the real gateway:
 * honours idempotency keys and times out ~10% of the time AFTER the charge has been captured.
 *
 * Test tokens:
 *   tok_visa          -> approved
 *   tok_declined      -> declined (card_declined)
 *   tok_insufficient  -> declined (insufficient_funds)
 */
@Component
public class MockPaymentGatewayClient implements PaymentGatewayClient {

    private static final Logger log = LoggerFactory.getLogger(MockPaymentGatewayClient.class);

    private final Map<String, GatewayResponse> byIdempotencyKey = new ConcurrentHashMap<>();
    private final Map<String, BigDecimal> capturedAmounts = new ConcurrentHashMap<>();

    @Override
    public GatewayResponse charge(GatewayChargeRequest request) {
        GatewayResponse previous = byIdempotencyKey.get(request.idempotencyKey());
        if (previous != null) {
            return previous;
        }
        GatewayResponse response;
        String token = request.paymentToken() == null ? "" : request.paymentToken();
        switch (token) {
            case "tok_declined" -> response = new GatewayResponse(false, null, "card_declined", "0002");
            case "tok_insufficient" -> response = new GatewayResponse(false, null, "insufficient_funds", "9995");
            default -> {
                String ref = "ch_" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
                capturedAmounts.put(ref, request.amount());
                response = new GatewayResponse(true, ref, null, "4242");
            }
        }
        byIdempotencyKey.put(request.idempotencyKey(), response);
        if (ThreadLocalRandom.current().nextInt(10) == 0) {
            log.warn("[mock-gateway] simulating read timeout after capture for key {}", request.idempotencyKey());
            throw new GatewayTimeoutException("Read timed out");
        }
        return response;
    }

    @Override
    public GatewayResponse refund(String chargeReference, BigDecimal amount, String idempotencyKey) {
        BigDecimal captured = capturedAmounts.get(chargeReference);
        if (captured == null) {
            return new GatewayResponse(false, null, "no_such_charge", null);
        }
        return new GatewayResponse(true, "re_" + UUID.randomUUID().toString().substring(0, 12), null, null);
    }

    /** Visible for ops tooling. */
    public int totalCharges() {
        return capturedAmounts.size();
    }
}
