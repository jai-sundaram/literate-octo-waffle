package com.meridian.commerce.payment;

import com.meridian.commerce.common.exception.PaymentException;
import com.meridian.commerce.common.exception.ResourceNotFoundException;
import com.meridian.commerce.config.MeridianProperties;
import com.meridian.commerce.payment.gateway.GatewayChargeRequest;
import com.meridian.commerce.payment.gateway.GatewayResponse;
import com.meridian.commerce.payment.gateway.GatewayTimeoutException;
import com.meridian.commerce.payment.gateway.PaymentGatewayClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentGatewayClient gateway;
    private final PaymentRepository payments;
    private final RefundService refundService;
    private final MeridianProperties properties;

    public PaymentService(PaymentGatewayClient gateway, PaymentRepository payments,
                          RefundService refundService, MeridianProperties properties) {
        this.gateway = gateway;
        this.payments = payments;
        this.refundService = refundService;
        this.properties = properties;
    }

    @Transactional
    public Payment charge(Long orderId, BigDecimal amount, String currency, String paymentToken) {
        log.info("Charging order {} amount {} {} using token {}", orderId, amount, currency, paymentToken);
        int maxRetries = properties.getPayments().getMaxRetries();
        GatewayResponse response = null;
        String idempotencyKey = null;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            idempotencyKey = "ord-" + orderId + "-" + UUID.randomUUID();
            try {
                response = gateway.charge(new GatewayChargeRequest(paymentToken, amount, currency, idempotencyKey));
                break;
            } catch (GatewayTimeoutException e) {
                log.warn("Gateway timeout for order {} (attempt {}/{})", orderId, attempt, maxRetries);
                sleepQuietly(200L * attempt);
            }
        }

        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setCurrency(currency);
        payment.setIdempotencyKey(idempotencyKey);

        if (response == null) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason("gateway_unavailable");
            payments.save(payment);
            throw new PaymentException("Payment gateway unavailable", true);
        }
        if (!response.approved()) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(response.declineCode());
            payments.save(payment);
            throw new PaymentException("Payment declined: " + response.declineCode(), false);
        }
        payment.setStatus(PaymentStatus.CAPTURED);
        payment.setGatewayReference(response.reference());
        payment.setCardLast4(response.cardLast4());
        return payments.save(payment);
    }

    @Transactional
    public void refundFull(Long orderId, String reason) {
        Payment payment = payments.findFirstByOrderIdAndStatus(orderId, PaymentStatus.CAPTURED)
                .orElseThrow(() -> new ResourceNotFoundException("Payment for order", orderId));
        refundService.refund(payment.getId(), payment.getAmount(), reason, "system");
    }

    private static void sleepQuietly(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
        }
    }
}
