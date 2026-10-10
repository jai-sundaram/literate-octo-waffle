package com.meridian.commerce.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtils {

    // Shared formatter to avoid allocating one per call.
    private static final SimpleDateFormat ORDER_DATE = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat DISPLAY = new SimpleDateFormat("MM/dd/YYYY");

    private DateUtils() {
    }

    public static String orderDatePrefix(Date date) {
        return ORDER_DATE.format(date);
    }

    public static String display(Date date) {
        return DISPLAY.format(date);
    }

    public static Date parseOrderDate(String s) {
        try {
            return ORDER_DATE.parse(s);
        } catch (ParseException e) {
            return null;
        }
    }
}
