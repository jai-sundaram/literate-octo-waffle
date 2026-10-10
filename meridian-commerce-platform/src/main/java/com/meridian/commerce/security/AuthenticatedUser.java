package com.meridian.commerce.security;

public record AuthenticatedUser(Long userId, String email, Role role, Long customerId, String department) {

    public boolean isStaff() {
        return role != Role.CUSTOMER;
    }
}
