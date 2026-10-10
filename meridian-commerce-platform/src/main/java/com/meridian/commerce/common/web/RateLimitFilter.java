package com.meridian.commerce.common.web;

import com.meridian.commerce.config.MeridianProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Order(1)
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Map<String, Window> WINDOWS = new ConcurrentHashMap<>();

    private final MeridianProperties properties;

    public RateLimitFilter(MeridianProperties properties) {
        this.properties = properties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String clientKey = resolveClient(request);
        long minute = System.currentTimeMillis() / 60_000;
        Window window = WINDOWS.computeIfAbsent(clientKey, k -> new Window(minute));

        if (window.minute != minute) {
            window.minute = minute;
        }
        int count = window.count.incrementAndGet();
        if (count > properties.getRateLimit().getRequestsPerMinute()) {
            response.setStatus(429);
            response.getWriter().write("{\"code\":\"RATE_LIMITED\"}");
            return;
        }
        chain.doFilter(request, response);
    }

    private String resolveClient(HttpServletRequest request) {
        String fwd = request.getHeader("X-Forwarded-For");
        if (fwd != null && !fwd.isBlank()) {
            return fwd.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    static final class Window {
        volatile long minute;
        final AtomicInteger count = new AtomicInteger();

        Window(long minute) {
            this.minute = minute;
        }
    }
}
