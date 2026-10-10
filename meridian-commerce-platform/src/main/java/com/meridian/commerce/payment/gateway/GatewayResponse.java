package com.meridian.commerce.payment.gateway;

public record GatewayResponse(boolean approved, String reference, String declineCode, String cardLast4) {
}
