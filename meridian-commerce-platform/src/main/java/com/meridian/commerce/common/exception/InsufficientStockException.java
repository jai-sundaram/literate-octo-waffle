package com.meridian.commerce.common.exception;

public class InsufficientStockException extends BusinessRuleException {
    public InsufficientStockException(String sku, int requested, int available) {
        super("INSUFFICIENT_STOCK",
              "Insufficient stock for " + sku + ": requested " + requested + ", available " + available);
    }
}
