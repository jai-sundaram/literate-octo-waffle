package com.meridian.commerce.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "meridian")
public class MeridianProperties {

    private final Tax tax = new Tax();
    private final Shipping shipping = new Shipping();
    private final Security security = new Security();
    private final Payments payments = new Payments();
    private final Loyalty loyalty = new Loyalty();
    private final RateLimit rateLimit = new RateLimit();

    public Tax getTax() { return tax; }
    public Shipping getShipping() { return shipping; }
    public Security getSecurity() { return security; }
    public Payments getPayments() { return payments; }
    public Loyalty getLoyalty() { return loyalty; }
    public RateLimit getRateLimit() { return rateLimit; }

    public static class Tax {
        private BigDecimal defaultRate = new BigDecimal("0.06");
        public BigDecimal getDefaultRate() { return defaultRate; }
        public void setDefaultRate(BigDecimal defaultRate) { this.defaultRate = defaultRate; }
    }

    public static class Shipping {
        private BigDecimal freeShippingThreshold = new BigDecimal("75.00");
        private BigDecimal baseRate = new BigDecimal("5.99");
        private BigDecimal perKgRate = new BigDecimal("1.25");
        public BigDecimal getFreeShippingThreshold() { return freeShippingThreshold; }
        public void setFreeShippingThreshold(BigDecimal v) { this.freeShippingThreshold = v; }
        public BigDecimal getBaseRate() { return baseRate; }
        public void setBaseRate(BigDecimal v) { this.baseRate = v; }
        public BigDecimal getPerKgRate() { return perKgRate; }
        public void setPerKgRate(BigDecimal v) { this.perKgRate = v; }
    }

    public static class Security {
        private String tokenSecret;
        private long tokenTtlSeconds = 3600;
        public String getTokenSecret() { return tokenSecret; }
        public void setTokenSecret(String tokenSecret) { this.tokenSecret = tokenSecret; }
        public long getTokenTtlSeconds() { return tokenTtlSeconds; }
        public void setTokenTtlSeconds(long tokenTtlSeconds) { this.tokenTtlSeconds = tokenTtlSeconds; }
    }

    public static class Payments {
        private String gatewayUrl;
        private int maxRetries = 3;
        public String getGatewayUrl() { return gatewayUrl; }
        public void setGatewayUrl(String gatewayUrl) { this.gatewayUrl = gatewayUrl; }
        public int getMaxRetries() { return maxRetries; }
        public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }
    }

    public static class Loyalty {
        private int pointsPerDollar = 10;
        public int getPointsPerDollar() { return pointsPerDollar; }
        public void setPointsPerDollar(int pointsPerDollar) { this.pointsPerDollar = pointsPerDollar; }
    }

    public static class RateLimit {
        private int requestsPerMinute = 120;
        public int getRequestsPerMinute() { return requestsPerMinute; }
        public void setRequestsPerMinute(int requestsPerMinute) { this.requestsPerMinute = requestsPerMinute; }
    }
}
