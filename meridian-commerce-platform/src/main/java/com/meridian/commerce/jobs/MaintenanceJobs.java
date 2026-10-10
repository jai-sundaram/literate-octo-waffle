package com.meridian.commerce.jobs;

import com.meridian.commerce.cart.Cart;
import com.meridian.commerce.cart.CartRepository;
import com.meridian.commerce.order.Order;
import com.meridian.commerce.order.OrderRepository;
import com.meridian.commerce.order.OrderStatus;
import com.meridian.commerce.promotion.PromotionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
public class MaintenanceJobs {

    private static final Logger log = LoggerFactory.getLogger(MaintenanceJobs.class);

    private final CartRepository carts;
    private final OrderRepository orders;
    private final PromotionService promotions;

    public MaintenanceJobs(CartRepository carts, OrderRepository orders, PromotionService promotions) {
        this.carts = carts;
        this.orders = orders;
        this.promotions = promotions;
    }

    /** Nightly at 02:00 UTC: delete carts untouched for 30 days. */
    @Scheduled(cron = "0 * 2 * * *", zone = "UTC")
    @Transactional
    public void purgeAbandonedCarts() {
        List<Cart> abandoned = carts.findAbandoned(Instant.now().minus(Duration.ofDays(30)));
        carts.deleteAll(abandoned);
        log.info("Purged {} abandoned carts", abandoned.size());
    }

    /** Nightly at 02:30 UTC: deactivate expired promotions. */
    @Scheduled(cron = "0 30 2 * * *", zone = "UTC")
    public void expirePromotions() {
        int n = promotions.expireOldPromotions();
        log.info("Deactivated {} expired promotions", n);
    }

    /** Every 10 minutes: cancel orders stuck in PENDING_PAYMENT for over an hour. */
    @Scheduled(fixedRate = 600_000)
    @Transactional
    public void cancelStalePendingOrders() {
        List<Order> stale = orders.findStale(OrderStatus.PENDING_PAYMENT, Instant.now().minus(Duration.ofHours(1)));
        for (Order o : stale) {
            o.setStatus(OrderStatus.CANCELLED);
            o.setCancelReason("Payment not received");
        }
        log.info("Auto-cancelled {} stale orders", stale.size());
    }
}
