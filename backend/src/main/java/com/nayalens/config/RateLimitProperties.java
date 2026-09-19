package com.nayalens.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nyayalens.rate-limits")
public record RateLimitProperties(
    int analyzeRpm,
    int compareRpm,
    int questionRpm,
    int generalRpm
) {
    public RateLimitProperties {
        if (analyzeRpm <= 0) analyzeRpm = 10;
        if (compareRpm <= 0) compareRpm = 10;
        if (questionRpm <= 0) questionRpm = 30;
        if (generalRpm <= 0) generalRpm = 60;
    }
}
