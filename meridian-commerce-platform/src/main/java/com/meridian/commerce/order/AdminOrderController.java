package com.meridian.commerce.order;

import com.meridian.commerce.order.dto.OrderResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;
    private final OrderMapper mapper;

    public AdminOrderController(OrderService orderService, OrderMapper mapper) {
        this.orderService = orderService;
        this.mapper = mapper;
    }

    @PostMapping("/{id}/ship")
    public OrderResponse ship(@PathVariable Long id, @RequestParam String trackingNumber) {
        return mapper.toResponse(orderService.markShipped(id, trackingNumber));
    }

    @PostMapping("/{id}/status")
    public OrderResponse transition(@PathVariable Long id, @RequestParam OrderStatus status) {
        return mapper.toResponse(orderService.transition(id, status));
    }
}
