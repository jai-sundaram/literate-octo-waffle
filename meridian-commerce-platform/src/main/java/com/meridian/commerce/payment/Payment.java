package com.meridian.commerce.payment;

import com.meridian.commerce.common.domain.AuditableEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "payments")
public class Payment extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "refunded_amount", precision = 12, scale = 2)
    private BigDecimal refundedAmount = BigDecimal.ZERO;

    private String currency;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(name = "gateway_reference")
    private String gatewayReference;

    @Column(name = "idempotency_key")
    private String idempotencyKey;

    @Column(name = "card_last4")
    private String cardLast4;

    @Column(name = "failure_reason")
    private String failureReason;

    public BigDecimal refundable() {
        return amount.subtract(refundedAmount);
    }

    public Long getId() { return id; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getRefundedAmount() { return refundedAmount; }
    public void setRefundedAmount(BigDecimal refundedAmount) { this.refundedAmount = refundedAmount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public String getGatewayReference() { return gatewayReference; }
    public void setGatewayReference(String gatewayReference) { this.gatewayReference = gatewayReference; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
    public String getCardLast4() { return cardLast4; }
    public void setCardLast4(String cardLast4) { this.cardLast4 = cardLast4; }
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
}
