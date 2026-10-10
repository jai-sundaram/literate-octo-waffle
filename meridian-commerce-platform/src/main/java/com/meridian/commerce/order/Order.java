package com.meridian.commerce.order;

import com.meridian.commerce.common.domain.AuditableEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "orders")
public class Order extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;

    @Column(name = "tenant_id")
    private String tenantId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderLine> lines = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING_PAYMENT;

    @Embedded
    private ShippingAddress shippingAddress;

    @Column(precision = 12, scale = 2)
    private BigDecimal subtotal;
    @Column(name = "promo_discount", precision = 12, scale = 2)
    private BigDecimal promoDiscount;
    @Column(name = "loyalty_discount", precision = 12, scale = 2)
    private BigDecimal loyaltyDiscount;
    @Column(precision = 12, scale = 2)
    private BigDecimal shipping;
    @Column(precision = 12, scale = 2)
    private BigDecimal tax;
    @Column(precision = 12, scale = 2)
    private BigDecimal total;

    private String currency = "USD";

    @Column(name = "promo_code")
    private String promoCode;

    @Column(name = "placed_at")
    private Instant placedAt;

    @Column(name = "shipped_at")
    private Instant shippedAt;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "cancel_reason")
    private String cancelReason;

    public void addLine(OrderLine line) {
        line.setOrder(this);
        lines.add(line);
    }

    public Long getId() { return id; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Set<OrderLine> getLines() { return lines; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public ShippingAddress getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(ShippingAddress shippingAddress) { this.shippingAddress = shippingAddress; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getPromoDiscount() { return promoDiscount; }
    public void setPromoDiscount(BigDecimal promoDiscount) { this.promoDiscount = promoDiscount; }
    public BigDecimal getLoyaltyDiscount() { return loyaltyDiscount; }
    public void setLoyaltyDiscount(BigDecimal loyaltyDiscount) { this.loyaltyDiscount = loyaltyDiscount; }
    public BigDecimal getShipping() { return shipping; }
    public void setShipping(BigDecimal shipping) { this.shipping = shipping; }
    public BigDecimal getTax() { return tax; }
    public void setTax(BigDecimal tax) { this.tax = tax; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getPromoCode() { return promoCode; }
    public void setPromoCode(String promoCode) { this.promoCode = promoCode; }
    public Instant getPlacedAt() { return placedAt; }
    public void setPlacedAt(Instant placedAt) { this.placedAt = placedAt; }
    public Instant getShippedAt() { return shippedAt; }
    public void setShippedAt(Instant shippedAt) { this.shippedAt = shippedAt; }
    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }
}
