package com.meridian.commerce.order;

import com.meridian.commerce.order.dto.OrderLineResponse;
import com.meridian.commerce.order.dto.OrderResponse;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order o) {
        return new OrderResponse(o.getId(), o.getOrderNumber(), o.getCustomerId(), o.getStatus(),
                o.getLines().stream()
                        .map(l -> new OrderLineResponse(l.getSku(), l.getProductName(), l.getQuantity(),
                                l.getUnitPrice(), l.getLineTotal()))
                        .toList(),
                o.getSubtotal(), o.getPromoDiscount(), o.getLoyaltyDiscount(), o.getShipping(), o.getTax(),
                o.getTotal(), o.getCurrency(), o.getPlacedAt(), o.getTrackingNumber());
    }
}
