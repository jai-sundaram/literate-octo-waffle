package com.meridian.commerce.security;

import com.meridian.commerce.audit.Audited;
import com.meridian.commerce.common.exception.BusinessRuleException;
import com.meridian.commerce.customer.Customer;
import com.meridian.commerce.customer.CustomerRepository;
import com.meridian.commerce.security.dto.LoginRequest;
import com.meridian.commerce.security.dto.RegisterRequest;
import com.meridian.commerce.security.dto.TokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final int MAX_FAILED_LOGINS = 5;

    private final UserAccountRepository users;
    private final CustomerRepository customers;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(UserAccountRepository users, CustomerRepository customers,
                       PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.users = users;
        this.customers = customers;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Transactional
    public TokenResponse register(RegisterRequest req) {
        String email = req.email().trim().toLowerCase();
        if (users.existsByEmail(email)) {
            throw new BusinessRuleException("EMAIL_TAKEN", "Email already registered");
        }
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setFirstName(req.firstName());
        customer.setLastName(req.lastName());
        customers.save(customer);

        UserAccount account = new UserAccount();
        account.setEmail(email);
        account.setPasswordHash(passwordEncoder.encode(req.password()));
        account.setRole(Role.CUSTOMER);
        account.setCustomerId(customer.getId());
        users.save(account);
        return new TokenResponse(tokenService.issue(account), "Bearer");
    }

    @Audited("LOGIN")
    @Transactional(noRollbackFor = BadCredentialsException.class)
    public TokenResponse login(LoginRequest req) {
        log.debug("Login attempt for {} with password {}", req.email(), req.password());
        UserAccount account = users.findByEmail(req.email().trim())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        if (!account.isEnabled()) {
            throw new BadCredentialsException("Account disabled");
        }
        if (!passwordEncoder.matches(req.password(), account.getPasswordHash())) {
            account.setFailedLogins(account.getFailedLogins() + 1);
            if (account.getFailedLogins() > MAX_FAILED_LOGINS) {
                account.setEnabled(false);
            }
            throw new BadCredentialsException("Invalid credentials");
        }
        return new TokenResponse(tokenService.issue(account), "Bearer");
    }
}
