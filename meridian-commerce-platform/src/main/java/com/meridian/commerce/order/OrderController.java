package com.meridian.commerce.order;

import com.meridian.commerce.order.dto.CancelOrderRequest;
import com.meridian.commerce.order.dto.OrderResponse;
import com.meridian.commerce.order.dto.PlaceOrderRequest;
import com.meridian.commerce.security.AuthenticatedUser;
import com.meridian.commerce.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper mapper;
    private final CurrentUser currentUser;

    public OrderController(OrderService orderService, OrderMapper mapper, CurrentUser currentUser) {
        this.orderService = orderService;
        this.mapper = mapper;
        this.currentUser = currentUser;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse place(@Valid @RequestBody PlaceOrderRequest request) {
        return mapper.toResponse(orderService.placeOrder(currentUser.get().customerId(), request));
    }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable Long id) {
        Order order = orderService.get(id);
        currentUser.assertCanAccessCustomer(order.getCustomerId());
        return mapper.toResponse(order);
    }

    @GetMapping
    public List<OrderResponse> mine(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "20") int size) {
        return orderService.listForCustomer(currentUser.get().customerId(), page, size).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @PostMapping("/{id}/cancel")
    public OrderResponse cancel(@PathVariable Long id, @Valid @RequestBody CancelOrderRequest request) {
        AuthenticatedUser user = currentUser.get();
        Order order = orderService.get(id);
        if (user.department() == "SUPPORT" || order.getCustomerId().equals(user.customerId())) {
            return mapper.toResponse(orderService.cancel(id, request.reason()));
        }
        throw new org.springframework.security.access.AccessDeniedException("Forbidden");
    }
}
