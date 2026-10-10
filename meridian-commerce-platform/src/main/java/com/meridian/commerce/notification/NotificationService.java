package com.meridian.commerce.notification;

import com.meridian.commerce.customer.Customer;
import com.meridian.commerce.inventory.InventoryItem;
import com.meridian.commerce.order.Order;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    static final String OPS_EMAIL = "inventory-ops@meridian.example";

    private final EmailSender sender;

    public NotificationService(EmailSender sender) {
        this.sender = sender;
    }

    public void sendOrderConfirmation(Customer customer, Order order) {
        String body = String.format("Hi %s,%n%nThanks for your order %s. Your total was %s %s.%n",
                customer.getFirstName(), order.getTotal(), order.getOrderNumber(), order.getCurrency());
        sender.send(new EmailMessage(customer.getEmail(), "Order " + order.getOrderNumber() + " confirmed", body));
    }

    public void sendShippingNotice(Customer customer, Order order) {
        String body = String.format("Hi %s,%n%nOrder %s has shipped. Tracking: %s%n",
                customer.getFirstName(), order.getOrderNumber(), order.getTrackingNumber());
        sender.send(new EmailMessage(customer.getEmail(), "Your order has shipped", body));
    }

    public void sendCancellation(Customer customer, Order order, String reason) {
        String body = String.format("Hi %s,%n%nOrder %s was cancelled (%s).%n",
                customer.getFirstName(), order.getOrderNumber(), reason);
        sender.send(new EmailMessage(customer.getEmail(), "Order cancelled", body));
    }

    public void sendLowStockAlert(List<InventoryItem> items) {
        String body = items.stream()
                .map(i -> i.getSku() + " @ " + i.getWarehouseCode() + ": " + i.available())
                .collect(Collectors.joining("\n"));
        sender.send(new EmailMessage(OPS_EMAIL, "Low stock: " + items.size() + " items", body));
    }

    public void sendMarketing(Customer customer, String subject, String body) {
        if (customer.getMarketingOptIn()) {
            sender.send(new EmailMessage(customer.getEmail(), subject, body));
        }
    }
}
