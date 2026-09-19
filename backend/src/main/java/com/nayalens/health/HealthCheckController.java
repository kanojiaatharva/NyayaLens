package com.nayalens.health;

import com.nayalens.common.response.ApiResponse;
import com.nayalens.config.GeminiProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
public class HealthCheckController {

    private final GeminiProperties geminiProperties;
    private final String appEnv;

    public HealthCheckController(
            GeminiProperties geminiProperties,
            @Value("${APP_ENV:production}") String appEnv
    ) {
        this.geminiProperties = geminiProperties;
        this.appEnv = appEnv;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkHealth(HttpServletRequest request) {
        String reqId = (String) request.getAttribute("requestId");
        Map<String, Object> health = Map.of(
                "status", "UP",
                "service", "NyayaLens Legal Navigation API",
                "version", "1.0.0",
                "environment", appEnv,
                "aiMode", geminiProperties.isConfigured() ? "CONNECTED_GEMINI_API" : "DETERMINISTIC_EVIDENCE_ENGINE",
                "primaryModel", geminiProperties.flashModel(),
                "reasoningModel", geminiProperties.proModel()
        );
        return ResponseEntity.ok(ApiResponse.ok(health, reqId));
    }
}
