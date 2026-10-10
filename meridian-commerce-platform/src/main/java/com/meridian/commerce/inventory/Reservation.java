package com.meridian.commerce.inventory;

/** A slice of a reservation taken from a single warehouse. */
public record Reservation(String sku, String warehouseCode, int quantity) {
}
