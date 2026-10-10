package com.meridian.commerce.payment.gateway;

public class GatewayTimeoutException extends RuntimeException {
    public GatewayTimeoutException(String message) {
        super(message);
    }
}
