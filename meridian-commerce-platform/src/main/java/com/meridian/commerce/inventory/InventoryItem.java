package com.meridian.commerce.inventory;

import jakarta.persistence.*;

@Entity
@Table(name = "inventory_items",
       uniqueConstraints = @UniqueConstraint(columnNames = {"sku", "warehouse_code"}))
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sku;

    @Column(name = "warehouse_code", nullable = false)
    private String warehouseCode;

    @Column(name = "on_hand")
    private int onHand;

    private int reserved;

    @Column(name = "reorder_point")
    private int reorderPoint = 10;

    public int available() {
        return onHand - reserved;
    }

    public Long getId() { return id; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getWarehouseCode() { return warehouseCode; }
    public void setWarehouseCode(String warehouseCode) { this.warehouseCode = warehouseCode; }
    public int getOnHand() { return onHand; }
    public void setOnHand(int onHand) { this.onHand = onHand; }
    public int getReserved() { return reserved; }
    public void setReserved(int reserved) { this.reserved = reserved; }
    public int getReorderPoint() { return reorderPoint; }
    public void setReorderPoint(int reorderPoint) { this.reorderPoint = reorderPoint; }
}
