package com.meridian.commerce.payment;

import com.meridian.commerce.common.domain.AuditableEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "refunds")
public class Refund extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_id", nullable = false)
    private Long paymentId;

    @Column(precision = 12, scale = 2)
    private BigDecimal amount;

    private String reason;

    @Column(name = "gateway_reference")
    private String gatewayReference;

    @Column(name = "requested_by")
    private String requestedBy;

    public Long getId() { return id; }
    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getGatewayReference() { return gatewayReference; }
    public void setGatewayReference(String gatewayReference) { this.gatewayReference = gatewayReference; }
    public String getRequestedBy() { return requestedBy; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }
}
