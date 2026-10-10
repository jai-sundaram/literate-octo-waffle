package com.meridian.commerce.reporting;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailySalesRow(LocalDate day, long orderCount, BigDecimal grossRevenue, BigDecimal discounts,
                            BigDecimal tax, BigDecimal shipping) {
}
