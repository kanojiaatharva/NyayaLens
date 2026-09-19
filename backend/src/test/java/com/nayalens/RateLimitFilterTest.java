package com.nayalens;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nayalens.config.RateLimitProperties;
import com.nayalens.security.RateLimitFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RateLimitFilterTest {

    private RateLimitFilter rateLimitFilter;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        RateLimitProperties properties = new RateLimitProperties(2, 2, 2, 5);
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        rateLimitFilter = new RateLimitFilter(properties, objectMapper);
        filterChain = mock(FilterChain.class);
    }

    @Test
    @DisplayName("Should permit requests within rate limit threshold")
    void testRequestsWithinLimit() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/documents/doc_1/analyze");
        request.setRemoteAddr("192.168.1.100");
        MockHttpServletResponse response = new MockHttpServletResponse();

        rateLimitFilter.doFilter(request, response, filterChain);

        assertEquals(200, response.getStatus());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should throttle requests and return HTTP 429 when threshold exceeded")
    void testRequestsExceedingLimit() throws ServletException, IOException {
        String clientIp = "192.168.1.200";

        // Send 2 allowed requests (maxRpm is 2 for analyze)
        for (int i = 0; i < 2; i++) {
            MockHttpServletRequest req = new MockHttpServletRequest("POST", "/api/v1/documents/doc_1/analyze");
            req.setRemoteAddr(clientIp);
            MockHttpServletResponse res = new MockHttpServletResponse();
            rateLimitFilter.doFilter(req, res, filterChain);
            assertEquals(200, res.getStatus());
        }

        // 3rd request must be blocked with HTTP 429
        MockHttpServletRequest blockedReq = new MockHttpServletRequest("POST", "/api/v1/documents/doc_1/analyze");
        blockedReq.setRemoteAddr(clientIp);
        MockHttpServletResponse blockedRes = new MockHttpServletResponse();
        rateLimitFilter.doFilter(blockedReq, blockedRes, filterChain);

        assertEquals(429, blockedRes.getStatus());
        String body = blockedRes.getContentAsString();
        org.junit.jupiter.api.Assertions.assertTrue(body.contains("RATE_LIMIT_EXCEEDED"));
    }

    @Test
    @DisplayName("Should reject malformed X-Forwarded-For header and fallback to remoteAddr")
    void testMalformedXForwardedForFallback() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/documents/doc_1/analyze");
        request.addHeader("X-Forwarded-For", "malicious_injection_payload; DROP TABLE;");
        request.setRemoteAddr("10.0.0.5");
        MockHttpServletResponse response = new MockHttpServletResponse();

        rateLimitFilter.doFilter(request, response, filterChain);

        assertEquals(200, response.getStatus());
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
