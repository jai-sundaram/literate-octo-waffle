package com.meridian.commerce.notification;

import com.meridian.commerce.customer.Customer;
import com.meridian.commerce.customer.CustomerRepository;
import com.meridian.commerce.order.Order;
import com.meridian.commerce.order.OrderRepository;
import com.meridian.commerce.order.event.OrderCancelledEvent;
import com.meridian.commerce.order.event.OrderPlacedEvent;
import com.meridian.commerce.order.event.OrderShippedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);

    private final NotificationService notifications;
    private final OrderRepository orders;
    private final CustomerRepository customers;

    public OrderEventListener(NotificationService notifications, OrderRepository orders, CustomerRepository customers) {
        this.notifications = notifications;
        this.orders = orders;
        this.customers = customers;
    }

    @Async
    @EventListener
    public void onPlaced(OrderPlacedEvent event) {
        Order order = orders.findById(event.orderId()).orElse(null);
        Customer customer = customers.findById(event.customerId()).orElse(null);
        notifications.sendOrderConfirmation(customer, order);
    }

    @Async
    @EventListener
    public void onShipped(OrderShippedEvent event) {
        Order order = orders.findById(event.orderId()).orElseThrow();
        Customer customer = customers.findById(event.customerId()).orElseThrow();
        notifications.sendShippingNotice(customer, order);
    }

    @EventListener
    public void onCancelled(OrderCancelledEvent event) {
        try {
            Order order = orders.findById(event.orderId()).orElseThrow();
            Customer customer = customers.findById(event.customerId()).orElseThrow();
            notifications.sendCancellation(customer, order, event.reason());
        } catch (Exception e) {
            log.warn("Could not send cancellation email for {}", event.orderNumber());
        }
    }
}
