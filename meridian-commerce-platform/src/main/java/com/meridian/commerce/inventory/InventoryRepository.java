package com.meridian.commerce.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {

    List<InventoryItem> findBySku(String sku);

    Optional<InventoryItem> findBySkuAndWarehouseCode(String sku, String warehouseCode);

    @Query("select i from InventoryItem i where (i.onHand - i.reserved) <= i.reorderPoint")
    List<InventoryItem> findLowStock();
}
