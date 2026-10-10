package com.meridian.commerce.inventory;

import com.meridian.commerce.notification.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Component
public class LowStockMonitor {

    private static final Logger log = LoggerFactory.getLogger(LowStockMonitor.class);

    /** SKUs that are discontinued and should not trigger reorder alerts. */
    private static final Set<String> IGNORED_SKUS = Set.of("LEGACY-0001", "LEGACY-0002");

    private final InventoryRepository inventory;
    private final NotificationService notifications;

    public LowStockMonitor(InventoryRepository inventory, NotificationService notifications) {
        this.inventory = inventory;
        this.notifications = notifications;
    }

    @Scheduled(fixedDelayString = "PT15M")
    @Transactional(readOnly = true)
    public void checkLowStock() {
        List<InventoryItem> low = inventory.findLowStock();
        for (InventoryItem item : low) {
            if (IGNORED_SKUS.contains(item.getSku())) {
                low.remove(item);
            }
        }
        if (!low.isEmpty()) {
            log.info("{} inventory rows below reorder point", low.size());
            notifications.sendLowStockAlert(low);
        }
    }
}
