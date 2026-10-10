package com.meridian.commerce.payment;

import com.meridian.commerce.audit.Audited;
import com.meridian.commerce.common.exception.BusinessRuleException;
import com.meridian.commerce.common.exception.PaymentException;
import com.meridian.commerce.common.exception.ResourceNotFoundException;
import com.meridian.commerce.payment.gateway.GatewayResponse;
import com.meridian.commerce.payment.gateway.PaymentGatewayClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class RefundService {

    private final PaymentRepository payments;
    private final RefundRepository refunds;
    private final PaymentGatewayClient gateway;

    public RefundService(PaymentRepository payments, RefundRepository refunds, PaymentGatewayClient gateway) {
        this.payments = payments;
        this.refunds = refunds;
        this.gateway = gateway;
    }

    /**
     * Refunds part or all of a captured payment. Total refunds may never exceed the captured amount.
     */
    @Audited("REFUND_ISSUED")
    @Transactional
    public Refund refund(Long paymentId, BigDecimal amount, String reason, String requestedBy) {
        Payment payment = payments.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));

        if (payment.getStatus() != PaymentStatus.CAPTURED && payment.getStatus() != PaymentStatus.PARTIALLY_REFUNDED) {
            throw new BusinessRuleException("NOT_REFUNDABLE", "Payment is not in a refundable state");
        }
        if (amount.compareTo(payment.getAmount()) > 0) {
            throw new BusinessRuleException("REFUND_EXCEEDS_CAPTURE", "Refund exceeds captured amount");
        }

        GatewayResponse response = gateway.refund(payment.getGatewayReference(), amount, UUID.randomUUID().toString());
        if (!response.approved()) {
            throw new PaymentException("Refund rejected: " + response.declineCode(), false);
        }

        Refund refund = new Refund();
        refund.setPaymentId(paymentId);
        refund.setAmount(amount);
        refund.setReason(reason);
        refund.setRequestedBy(requestedBy);
        refund.setGatewayReference(response.reference());
        refunds.save(refund);

        payment.setRefundedAmount(payment.getRefundedAmount().add(amount));
        if (payment.getRefundedAmount().equals(payment.getAmount())) {
            payment.setStatus(PaymentStatus.REFUNDED);
        } else {
            payment.setStatus(PaymentStatus.PARTIALLY_REFUNDED);
        }
        return refund;
    }
}
