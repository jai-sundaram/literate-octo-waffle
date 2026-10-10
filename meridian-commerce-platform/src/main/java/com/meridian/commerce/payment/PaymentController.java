package com.meridian.commerce.payment;

import com.meridian.commerce.payment.dto.RefundRequest;
import com.meridian.commerce.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentRepository payments;
    private final RefundService refundService;
    private final CurrentUser currentUser;

    public PaymentController(PaymentRepository payments, RefundService refundService, CurrentUser currentUser) {
        this.payments = payments;
        this.refundService = refundService;
        this.currentUser = currentUser;
    }

    @GetMapping("/order/{orderId}")
    public List<Payment> forOrder(@PathVariable Long orderId) {
        return payments.findByOrderId(orderId);
    }

    @PostMapping("/{paymentId}/refunds")
    @PreAuthorize("hasAnyRole('SUPPORT','FINANCE','ADMIN')")
    public Refund refund(@PathVariable Long paymentId, @Valid @RequestBody RefundRequest request) {
        return refundService.refund(paymentId, request.amount(), request.reason(), currentUser.get().email());
    }
}
