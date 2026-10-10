package com.meridian.commerce.security;

import com.meridian.commerce.config.MeridianProperties;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class TokenServiceTest {

    @Test
    void roundTrip() {
        MeridianProperties props = new MeridianProperties();
        props.getSecurity().setTokenSecret("unit-test-secret");
        TokenService tokens = new TokenService(props);

        UserAccount u = new UserAccount();
        ReflectionTestUtils.setField(u, "id", 42L);
        u.setEmail("someone@example.com");
        u.setRole(Role.SUPPORT);
        u.setDepartment("SUPPORT");

        var verified = tokens.verify(tokens.issue(u));

        assertThat(verified).isPresent();
        assertThat(verified.get().email()).isEqualTo("someone@example.com");
        assertThat(verified.get().role()).isEqualTo(Role.SUPPORT);
    }

    @Test
    void rejectsTamperedToken() {
        MeridianProperties props = new MeridianProperties();
        props.getSecurity().setTokenSecret("unit-test-secret");
        TokenService tokens = new TokenService(props);
        assertThat(tokens.verify("abc.def")).isEmpty();
    }
}
