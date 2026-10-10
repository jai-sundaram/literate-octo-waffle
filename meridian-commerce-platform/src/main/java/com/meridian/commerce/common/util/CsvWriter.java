package com.meridian.commerce.common.util;

import java.util.List;

public final class CsvWriter {

    private CsvWriter() {
    }

    public static String toCsv(List<String> header, List<List<Object>> rows) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(",", header)).append("\n");
        for (List<Object> row : rows) {
            for (int i = 0; i < row.size(); i++) {
                Object v = row.get(i);
                sb.append(v == null ? "" : v.toString());
                if (i < row.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
