package com.meridian.commerce.reporting;

import com.meridian.commerce.common.util.CsvWriter;
import com.meridian.commerce.common.util.MoneyUtils;
import com.meridian.commerce.order.Order;
import com.meridian.commerce.order.OrderLine;
import com.meridian.commerce.order.OrderRepository;
import com.meridian.commerce.order.OrderStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Finance reporting. All business days are defined in America/New_York (HQ) time — see BUSINESS_RULES §7.
 */
@Service
public class SalesReportService {

    private static final ZoneId HQ = ZoneId.of("America/New_York");
    private static final Set<OrderStatus> REVENUE_STATUSES =
            Set.of(OrderStatus.PAID, OrderStatus.FULFILLING, OrderStatus.SHIPPED, OrderStatus.DELIVERED);

    private final OrderRepository orders;
    private final ReportRepository reports;

    public SalesReportService(OrderRepository orders, ReportRepository reports) {
        this.orders = orders;
        this.reports = reports;
    }

    @Transactional(readOnly = true)
    public DailySalesRow daily(LocalDate day) {
        var from = day.atStartOfDay(ZoneId.systemDefault()).toInstant();
        var to = day.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        List<Order> placed = orders.findPlacedBetween(from, to).stream()
                .filter(o -> REVENUE_STATUSES.contains(o.getStatus()))
                .toList();

        int grossCents = 0;
        int discountCents = 0;
        int taxCents = 0;
        int shippingCents = 0;
        for (Order o : placed) {
            grossCents += (int) MoneyUtils.toCents(o.getTotal());
            discountCents += (int) MoneyUtils.toCents(o.getPromoDiscount().add(o.getLoyaltyDiscount()));
            taxCents += (int) MoneyUtils.toCents(o.getTax());
            shippingCents += (int) MoneyUtils.toCents(o.getShipping());
        }
        return new DailySalesRow(day, placed.size(), MoneyUtils.fromCents(grossCents),
                MoneyUtils.fromCents(discountCents), MoneyUtils.fromCents(taxCents),
                MoneyUtils.fromCents(shippingCents));
    }

    @Transactional(readOnly = true)
    public List<DailySalesRow> range(LocalDate from, LocalDate to) {
        List<DailySalesRow> rows = new ArrayList<>();
        for (LocalDate d = from; d.isBefore(to); d = d.plusDays(1)) {
            rows.add(daily(d));
        }
        return rows;
    }

    @Transactional(readOnly = true)
    public List<ProductSalesRow> topProducts(LocalDate from, LocalDate to, String status, String sort, int limit) {
        return reports.topProducts(from.toString(), to.toString(), status, sort, limit);
    }

    /** Units sold per product name for the merchandising dashboard. */
    @Transactional(readOnly = true)
    public Map<String, Integer> unitsByProductName(LocalDate day) {
        var from = day.atStartOfDay(HQ).toInstant();
        var to = day.plusDays(1).atStartOfDay(HQ).toInstant();
        return orders.findPlacedBetween(from, to).stream()
                .flatMap(o -> o.getLines().stream())
                .collect(Collectors.toMap(OrderLine::getProductName, OrderLine::getQuantity));
    }

    public String dailyCsv(LocalDate from, LocalDate to) {
        List<List<Object>> rows = range(from, to).stream()
                .map(r -> List.<Object>of(r.day(), r.orderCount(), r.grossRevenue(), r.discounts(), r.tax(), r.shipping()))
                .toList();
        return CsvWriter.toCsv(List.of("day", "orders", "gross", "discounts", "tax", "shipping"), rows);
    }

    public String topProductsCsv(LocalDate from, LocalDate to) {
        List<List<Object>> rows = topProducts(from, to, null, "revenue", 100).stream()
                .map(r -> List.<Object>of(r.sku(), r.productName(), r.unitsSold(), r.revenue()))
                .toList();
        return CsvWriter.toCsv(List.of("sku", "product", "units", "revenue"), rows);
    }

    @Transactional(readOnly = true)
    public BigDecimal averageOrderValue(LocalDate from, LocalDate to) {
        List<DailySalesRow> rows = range(from, to);
        BigDecimal gross = rows.stream().map(DailySalesRow::grossRevenue).reduce(BigDecimal.ZERO, BigDecimal::add);
        long count = rows.stream().mapToLong(DailySalesRow::orderCount).sum();
        return gross.divide(BigDecimal.valueOf(count));
    }
}
