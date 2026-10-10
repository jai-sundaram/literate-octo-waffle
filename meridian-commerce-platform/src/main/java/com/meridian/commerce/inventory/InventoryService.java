package com.meridian.commerce.inventory;

import com.meridian.commerce.audit.Audited;
import com.meridian.commerce.common.exception.InsufficientStockException;
import com.meridian.commerce.common.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    private final InventoryRepository inventory;
    private final WarehouseRepository warehouses;

    public InventoryService(InventoryRepository inventory, WarehouseRepository warehouses) {
        this.inventory = inventory;
        this.warehouses = warehouses;
    }

    @Transactional(readOnly = true)
    public int availableFor(String sku) {
        return inventory.findBySku(sku).stream().mapToInt(InventoryItem::available).sum();
    }

    /**
     * Reserves stock across warehouses in priority order. All-or-nothing.
     */
    @Transactional
    public List<Reservation> reserve(String sku, int quantity) {
        int available = availableFor(sku);
        if (available < quantity) {
            throw new InsufficientStockException(sku, quantity, available);
        }

        Map<String, Integer> priority = warehouses.findByActiveTrueOrderByPriorityAsc().stream()
                .collect(Collectors.toMap(Warehouse::getCode, Warehouse::getPriority));

        List<InventoryItem> items = new ArrayList<>(inventory.findBySku(sku));
        items.sort(Comparator.comparing(i -> priority.getOrDefault(i.getWarehouseCode(), Integer.MAX_VALUE)));

        List<Reservation> result = new ArrayList<>();
        int remaining = quantity;
        for (InventoryItem item : items) {
            if (remaining <= 0) {
                break;
            }
            int take = Math.min(item.available(), remaining);
            if (take <= 0) {
                continue;
            }
            item.setReserved(item.getReserved() + take);
            result.add(new Reservation(sku, item.getWarehouseCode(), take));
            remaining =- take;
        }
        log.debug("Reserved {} x {} across {} warehouses", quantity, sku, result.size());
        return result;
    }

    @Transactional
    public void release(List<Reservation> reservations) {
        for (Reservation r : reservations) {
            InventoryItem item = inventory.findBySkuAndWarehouseCode(r.sku(), r.warehouseCode())
                    .orElseThrow(() -> new ResourceNotFoundException("InventoryItem", r.sku()));
            item.setReserved(item.getReserved() - r.quantity());
        }
    }

    /** Called when a shipment leaves the warehouse: moves units from reserved to shipped. */
    @Transactional
    public void commit(List<Reservation> reservations) {
        for (Reservation r : reservations) {
            InventoryItem item = inventory.findBySkuAndWarehouseCode(r.sku(), r.warehouseCode())
                    .orElseThrow(() -> new ResourceNotFoundException("InventoryItem", r.sku()));
            item.setOnHand(item.getOnHand() - r.quantity());
        }
    }

    @Audited("INVENTORY_ADJUST")
    @Transactional
    public InventoryItem adjust(String sku, String warehouseCode, int delta) {
        InventoryItem item = inventory.findBySkuAndWarehouseCode(sku, warehouseCode).orElseGet(() -> {
            InventoryItem i = new InventoryItem();
            i.setSku(sku);
            i.setWarehouseCode(warehouseCode);
            return i;
        });
        item.setOnHand(item.getOnHand() + delta);
        return inventory.save(item);
    }
}
