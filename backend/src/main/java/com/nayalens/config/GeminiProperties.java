package com.nayalens.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nyayalens.gemini")
public record GeminiProperties(
    String apiKey,
    String flashModel,
    String proModel,
    int timeoutSeconds,
    int maxRetries
) {
    public GeminiProperties {
        if (flashModel == null || flashModel.isBlank()) {
            flashModel = "gemini-3.8-flash";
        }
        if (proModel == null || proModel.isBlank()) {
            proModel = "gemini-3.1-pro-preview";
        }
        if (timeoutSeconds <= 0) {
            timeoutSeconds = 30;
        }
        if (maxRetries < 0) {
            maxRetries = 2;
        }
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank() && !apiKey.equals("your_gemini_api_key_here");
    }
}
