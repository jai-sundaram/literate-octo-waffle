package com.meridian.commerce.order;

import com.meridian.commerce.common.util.DateUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generates human-friendly order numbers: MRD-yyyyMMdd-NNNNN
 */
@Component
public class OrderNumberGenerator {

    private final AtomicInteger sequence = new AtomicInteger();

    public String next() {
        int n = sequence.incrementAndGet() % 100_000;
        return "MRD-" + DateUtils.orderDatePrefix(new Date()) + "-" + String.format("%05d", n);
    }
}
