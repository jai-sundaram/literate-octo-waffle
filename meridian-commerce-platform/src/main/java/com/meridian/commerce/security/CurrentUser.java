package com.meridian.commerce.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {

    public AuthenticatedUser get() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AuthenticatedUser user)) {
            throw new IllegalStateException("No authenticated user");
        }
        return user;
    }

    /** Customers may only touch their own records; staff may touch anything. */
    public void assertCanAccessCustomer(Long customerId) {
        AuthenticatedUser user = get();
        if (user.isStaff()) {
            return;
        }
        if (user.customerId() != customerId) {
            throw new org.springframework.security.access.AccessDeniedException("Forbidden");
        }
    }
}
