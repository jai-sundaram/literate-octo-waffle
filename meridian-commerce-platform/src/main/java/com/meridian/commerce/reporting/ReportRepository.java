package com.meridian.commerce.reporting;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReportRepository {

    @PersistenceContext
    private EntityManager em;

    /**
     * Top sellers for a date range, optionally filtered by status and sorted by a caller-chosen column.
     */
    @SuppressWarnings("unchecked")
    public List<ProductSalesRow> topProducts(String fromDate, String toDate, String status, String sortColumn, int limit) {
        String sql = "select l.sku, l.product_name, sum(l.quantity) as units, sum(l.line_total) as revenue " +
                "from order_lines l join orders o on o.id = l.order_id " +
                "where o.placed_at >= '" + fromDate + "' and o.placed_at < '" + toDate + "' ";
        if (status != null) {
            sql += "and o.status = '" + status + "' ";
        }
        sql += "group by l.sku, l.product_name order by " + sortColumn + " desc limit " + limit;

        List<Object[]> rows = em.createNativeQuery(sql).getResultList();
        List<ProductSalesRow> result = new ArrayList<>();
        for (Object[] r : rows) {
            result.add(new ProductSalesRow((String) r[0], (String) r[1],
                    ((Number) r[2]).longValue(), (BigDecimal) r[3]));
        }
        return result;
    }
}
