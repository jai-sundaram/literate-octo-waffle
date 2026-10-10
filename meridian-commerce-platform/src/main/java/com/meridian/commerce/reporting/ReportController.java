package com.meridian.commerce.reporting;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final SalesReportService reportService;

    public ReportController(SalesReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/daily")
    public List<DailySalesRow> daily(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return reportService.range(from, to);
    }

    @GetMapping("/top-products")
    public List<ProductSalesRow> topProducts(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                                             @RequestParam(required = false) String status,
                                             @RequestParam(defaultValue = "revenue") String sort,
                                             @RequestParam(defaultValue = "20") int limit) {
        return reportService.topProducts(from, to, status, sort, limit);
    }

    @GetMapping("/units-by-product")
    public Map<String, Integer> units(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day) {
        return reportService.unitsByProductName(day);
    }

    @GetMapping("/aov")
    public Map<String, BigDecimal> aov(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return Map.of("averageOrderValue", reportService.averageOrderValue(from, to));
    }

    @GetMapping(value = "/top-products.csv", produces = "text/csv")
    public ResponseEntity<String> topProductsCsv(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=top-products.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(reportService.topProductsCsv(from, to));
    }
}
