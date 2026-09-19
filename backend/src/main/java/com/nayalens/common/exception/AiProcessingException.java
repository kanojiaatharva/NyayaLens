package com.nayalens.common.exception;

import org.springframework.http.HttpStatus;

public class AiProcessingException extends ApiException {
    public AiProcessingException(String message) {
        super(HttpStatus.SERVICE_UNAVAILABLE, "AI_PROCESSING_ERROR", message);
    }

    public AiProcessingException(String message, Throwable cause) {
        super(HttpStatus.SERVICE_UNAVAILABLE, "AI_PROCESSING_ERROR", message, cause);
    }
}
