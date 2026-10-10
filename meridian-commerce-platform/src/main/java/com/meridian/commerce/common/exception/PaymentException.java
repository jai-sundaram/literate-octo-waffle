package com.meridian.commerce.common.exception;

public class PaymentException extends RuntimeException {
    private final boolean retryable;

    public PaymentException(String message, boolean retryable) {
        super(message);
        this.retryable = retryable;
    }

    public PaymentException(String message, boolean retryable, Throwable cause) {
        super(message, cause);
        this.retryable = retryable;
    }

    public boolean isRetryable() {
        return retryable;
    }
}
