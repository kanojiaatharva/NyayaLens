package com.nayalens.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nayalens.common.response.ApiResponse;
import com.nayalens.config.RateLimitProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitProperties properties;
    private final ObjectMapper objectMapper;
    private final Map<String, RequestCounter> requestCounts = new ConcurrentHashMap<>();

    public RateLimitFilter(RateLimitProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    private static class RequestCounter {
        final long windowStart;
        final AtomicInteger count;

        RequestCounter(long windowStart) {
            this.windowStart = windowStart;
            this.count = new AtomicInteger(1);
        }
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String clientIp = getClientIp(request);
        String path = request.getRequestURI();

        int maxRpm = resolveMaxRpm(path);
        String rateKey = clientIp + ":" + getEndpointCategory(path);
        long currentMinute = System.currentTimeMillis() / 60000;

        RequestCounter counter = requestCounts.compute(rateKey, (k, existing) -> {
            if (existing == null || existing.windowStart != currentMinute) {
                return new RequestCounter(currentMinute);
            }
            existing.count.incrementAndGet();
            return existing;
        });

        if (counter.count.get() > maxRpm) {
            String reqId = (String) request.getAttribute("requestId");
            if (reqId == null) reqId = "req_ratelimit";

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            ApiResponse<Void> err = ApiResponse.error(
                    HttpStatus.TOO_MANY_REQUESTS.value(),
                    "RATE_LIMIT_EXCEEDED",
                    "Rate limit exceeded. Maximum " + maxRpm + " requests per minute allowed for this endpoint.",
                    reqId
            );
            response.getWriter().write(objectMapper.writeValueAsString(err));
            return;
        }

        // Clean up old window entries periodically
        if (requestCounts.size() > 5000) {
            requestCounts.entrySet().removeIf(e -> e.getValue().windowStart < currentMinute);
        }

        filterChain.doFilter(request, response);
    }

    private int resolveMaxRpm(String path) {
        if (path.contains("/analyze")) return properties.analyzeRpm();
        if (path.contains("/compare")) return properties.compareRpm();
        if (path.contains("/questions")) return properties.questionRpm();
        return properties.generalRpm();
    }

    private String getEndpointCategory(String path) {
        if (path.contains("/analyze")) return "analyze";
        if (path.contains("/compare")) return "compare";
        if (path.contains("/questions")) return "questions";
        return "general";
    }

    private String getClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr() != null ? request.getRemoteAddr() : "127.0.0.1";
    }
}
