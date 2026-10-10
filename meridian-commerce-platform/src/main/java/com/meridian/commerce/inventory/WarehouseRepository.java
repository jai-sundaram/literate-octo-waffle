package com.meridian.commerce.inventory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WarehouseRepository extends JpaRepository<Warehouse, String> {
    List<Warehouse> findByActiveTrueOrderByPriorityAsc();
}
