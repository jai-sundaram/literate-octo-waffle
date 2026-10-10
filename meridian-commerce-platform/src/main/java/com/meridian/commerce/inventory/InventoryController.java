package com.meridian.commerce.inventory;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{sku}")
    public Map<String, Object> available(@PathVariable String sku) {
        return Map.of("sku", sku, "available", inventoryService.availableFor(sku));
    }

    @PostMapping("/{sku}/adjust")
    @PreAuthorize("hasRole('ADMIN')")
    public InventoryItem adjust(@PathVariable String sku,
                                @RequestParam String warehouse,
                                @RequestParam int delta) {
        return inventoryService.adjust(sku, warehouse, delta);
    }
}
