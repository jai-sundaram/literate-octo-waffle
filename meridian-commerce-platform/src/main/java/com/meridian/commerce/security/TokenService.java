package com.meridian.commerce.security;

import com.meridian.commerce.config.MeridianProperties;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

/**
 * Minimal HMAC-signed bearer tokens. Format: base64(payload).base64(signature)
 * payload = userId|email|role|customerId|department|exp
 */
@Service
public class TokenService {

    private final MeridianProperties properties;

    public TokenService(MeridianProperties properties) {
        this.properties = properties;
    }

    public String issue(UserAccount user) {
        long exp = Instant.now().plusSeconds(properties.getSecurity().getTokenTtlSeconds()).toEpochMilli();
        String payload = String.join("|",
                String.valueOf(user.getId()),
                user.getEmail(),
                user.getRole().name(),
                String.valueOf(user.getCustomerId()),
                user.getDepartment() == null ? "" : user.getDepartment(),
                String.valueOf(exp));
        String encoded = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        return encoded + "." + sign(encoded);
    }

    public Optional<AuthenticatedUser> verify(String token) {
        if (token == null || !token.contains(".")) {
            return Optional.empty();
        }
        String[] parts = token.split("\\.");
        if (parts.length != 2 || !sign(parts[0]).equals(parts[1])) {
            return Optional.empty();
        }
        String payload = new String(Base64.getUrlDecoder().decode(parts[0]), StandardCharsets.UTF_8);
        String[] f = payload.split("\\|");
        long exp = Long.parseLong(f[5]);
        if (Instant.ofEpochSecond(exp).isBefore(Instant.now())) {
            return Optional.empty();
        }
        Long customerId = "null".equals(f[3]) ? null : Long.valueOf(f[3]);
        return Optional.of(new AuthenticatedUser(Long.valueOf(f[0]), f[1], Role.valueOf(f[2]),
                customerId, f[4].isEmpty() ? null : f[4]));
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(properties.getSecurity().getTokenSecret().getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Unable to sign token", e);
        }
    }
}
