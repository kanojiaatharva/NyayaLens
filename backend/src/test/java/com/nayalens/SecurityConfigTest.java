package com.nayalens;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nayalens.config.RateLimitProperties;
import com.nayalens.config.SecurityConfig;
import com.nayalens.security.RateLimitFilter;
import com.nayalens.security.SecurityHeadersFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class SecurityConfigTest {

    @Test
    @DisplayName("Should instantiate SecurityConfig with rate limiter and security headers")
    void testSecurityConfigInitialization() {
        RateLimitProperties props = new RateLimitProperties(5, 5, 5, 10);
        ObjectMapper mapper = new ObjectMapper();
        RateLimitFilter rateLimitFilter = new RateLimitFilter(props, mapper);
        SecurityHeadersFilter securityHeadersFilter = new SecurityHeadersFilter();
        SecurityConfig securityConfig = new SecurityConfig(rateLimitFilter, securityHeadersFilter);

        assertNotNull(securityConfig);
    }
}
