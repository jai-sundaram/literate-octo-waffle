package com.meridian.commerce.common.tenant;

public final class TenantContext {

    public static final String DEFAULT_TENANT = "meridian-us";
    private static final ThreadLocal<String> CURRENT = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void set(String tenantId) {
        CURRENT.set(tenantId);
    }

    public static String get() {
        String t = CURRENT.get();
        return t == null ? DEFAULT_TENANT : t;
    }

    public static void clear() {
        CURRENT.remove();
    }
}
