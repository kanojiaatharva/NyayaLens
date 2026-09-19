package com.nayalens.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nayalens.config.GeminiProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

@Component
public class GeminiClient {

    private static final Logger log = LoggerFactory.getLogger(GeminiClient.class);
    private static final String GEMINI_API_BASE = "https://generativelanguage.googleapis.com/v1beta/models/";

    private final GeminiProperties properties;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final DeterministicFallbackProvider fallbackProvider;

    public GeminiClient(GeminiProperties properties, ObjectMapper objectMapper, DeterministicFallbackProvider fallbackProvider) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.fallbackProvider = fallbackProvider;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public enum ModelTier {
        FLASH,
        PRO
    }

    public String generateStructuredJson(String prompt, ModelTier tier) {
        if (!properties.isConfigured()) {
            log.info("Gemini API key not provided. Utilizing high-fidelity deterministic fallback engine.");
            return null; // Signals caller to use fallback
        }

        String modelName = (tier == ModelTier.PRO) ? properties.proModel() : properties.flashModel();
        String url = GEMINI_API_BASE + modelName + ":generateContent?key=" + properties.apiKey();

        int maxRetries = properties.maxRetries();
        int attempt = 0;

        while (attempt <= maxRetries) {
            attempt++;
            try {
                // Construct request body with system instruction and user prompt
                var requestPayload = Map.of(
                        "system_instruction", Map.of(
                                "parts", Map.of("text", PromptRegistry.SYSTEM_INSTRUCTION)
                        ),
                        "contents", Map.of(
                                "parts", Map.of("text", prompt)
                        ),
                        "generationConfig", Map.of(
                                "response_mime_type", "application/json",
                                "temperature", 0.1
                        )
                );

                String jsonBody = objectMapper.writeValueAsString(requestPayload);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .timeout(Duration.ofSeconds(properties.timeoutSeconds()))
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                log.info("Dispatching server-side AI request to model [model={}, tier={}, attempt={}]", modelName, tier, attempt);

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    JsonNode root = objectMapper.readTree(response.body());
                    JsonNode candidates = root.path("candidates");
                    if (candidates.isArray() && !candidates.isEmpty()) {
                        String text = candidates.get(0).path("content").path("parts").get(0).path("text").asText();
                        return cleanJsonResponse(text);
                    }
                } else if (response.statusCode() == 429 || response.statusCode() >= 500) {
                    log.warn("Gemini API transient failure [status={}]. Attempt {}/{}", response.statusCode(), attempt, maxRetries + 1);
                    if (attempt <= maxRetries) {
                        Thread.sleep(1000L * attempt);
                        continue;
                    }
                } else {
                    log.error("Gemini API non-retryable response [status={}]. Falling back gracefully.", response.statusCode());
                    break;
                }
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                log.warn("Gemini API call interrupted.");
                break;
            } catch (Exception e) {
                log.warn("Error calling Gemini API [attempt={}/{}]: {}", attempt, maxRetries + 1, e.getMessage());
                if (attempt <= maxRetries) {
                    try { Thread.sleep(1000L * attempt); } catch (InterruptedException ignored) {}
                }
            }
        }

        log.info("Returning null to invoke deterministic fallback provider after AI client exhaustion.");
        return null;
    }

    private String cleanJsonResponse(String raw) {
        if (raw == null) return null;
        String trimmed = raw.trim();
        if (trimmed.startsWith("```json")) {
            trimmed = trimmed.substring(7);
        } else if (trimmed.startsWith("```")) {
            trimmed = trimmed.substring(3);
        }
        if (trimmed.endsWith("```")) {
            trimmed = trimmed.substring(0, trimmed.length() - 3);
        }
        return trimmed.trim();
    }

    public DeterministicFallbackProvider getFallbackProvider() {
        return fallbackProvider;
    }
}
