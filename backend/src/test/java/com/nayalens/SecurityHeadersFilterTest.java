package com.nayalens;

import com.nayalens.security.SecurityHeadersFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityHeadersFilterTest {

    private SecurityHeadersFilter securityHeadersFilter;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        securityHeadersFilter = new SecurityHeadersFilter();
        filterChain = mock(FilterChain.class);
    }

    @Test
    @DisplayName("Should inject all required OWASP security headers")
    void testStandardSecurityHeaders() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/health");
        MockHttpServletResponse response = new MockHttpServletResponse();

        securityHeadersFilter.doFilter(request, response, filterChain);

        assertEquals("nosniff", response.getHeader("X-Content-Type-Options"));
        assertEquals("DENY", response.getHeader("X-Frame-Options"));
        assertEquals("1; mode=block", response.getHeader("X-XSS-Protection"));
        assertEquals("strict-origin-when-cross-origin", response.getHeader("Referrer-Policy"));
        assertEquals("max-age=31536000; includeSubDomains; preload", response.getHeader("Strict-Transport-Security"));
        assertEquals("same-origin", response.getHeader("Cross-Origin-Opener-Policy"));
        assertEquals("same-origin", response.getHeader("Cross-Origin-Resource-Policy"));
        assertEquals("none", response.getHeader("X-Permitted-Cross-Domain-Policies"));

        String csp = response.getHeader("Content-Security-Policy");
        assertNotNull(csp);
        assertTrue(csp.contains("frame-ancestors 'none'"));
        assertTrue(csp.contains("object-src 'none'"));

        assertNotNull(response.getHeader("X-Request-Id"));
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should enforce no-store cache control for confidential legal API endpoints")
    void testApiCacheControlHeaders() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/documents/doc_123/analyze");
        MockHttpServletResponse response = new MockHttpServletResponse();

        securityHeadersFilter.doFilter(request, response, filterChain);

        assertEquals("no-store, no-cache, max-age=0, must-revalidate", response.getHeader("Cache-Control"));
        assertEquals("no-cache", response.getHeader("Pragma"));
        assertEquals("0", response.getHeader("Expires"));
    }

    @Test
    @DisplayName("Should preserve incoming X-Request-Id header for end-to-end tracing")
    void testPreservesIncomingRequestId() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/health");
        request.addHeader("X-Request-Id", "req_custom_trace_999");
        MockHttpServletResponse response = new MockHttpServletResponse();

        securityHeadersFilter.doFilter(request, response, filterChain);

        assertEquals("req_custom_trace_999", response.getHeader("X-Request-Id"));
        assertEquals("req_custom_trace_999", request.getAttribute("requestId"));
    }
}
