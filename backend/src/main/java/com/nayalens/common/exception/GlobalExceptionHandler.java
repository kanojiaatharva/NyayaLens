package com.nayalens.common.exception;

import com.nayalens.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private String getRequestId(HttpServletRequest request) {
        Object reqId = request.getAttribute("requestId");
        if (reqId instanceof String s && !s.isBlank()) {
            return s;
        }
        return "req_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException ex, HttpServletRequest request) {
        String reqId = getRequestId(request);
        log.warn("Handled API exception [reqId={}]: code={}, message={}", reqId, ex.getCode(), ex.getMessage());
        ApiResponse<Void> response = ApiResponse.error(ex.getStatus().value(), ex.getCode(), ex.getMessage(), reqId);
        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String reqId = getRequestId(request);
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("Validation failure [reqId={}]: {}", reqId, details);
        ApiResponse<Void> response = ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "VALIDATION_ERROR", details, reqId);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        String reqId = getRequestId(request);
        log.warn("Max upload size exceeded [reqId={}]: {}", reqId, ex.getMessage());
        ApiResponse<Void> response = ApiResponse.error(
                HttpStatus.PAYLOAD_TOO_LARGE.value(),
                "PAYLOAD_TOO_LARGE",
                "Uploaded file exceeds the maximum allowed size limit.",
                reqId
        );
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        String reqId = getRequestId(request);
        log.warn("Illegal argument [reqId={}]: {}", reqId, ex.getMessage());
        ApiResponse<Void> response = ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "INVALID_ARGUMENT", ex.getMessage(), reqId);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex, HttpServletRequest request) {
        String reqId = getRequestId(request);
        log.error("Unhandled unexpected error [reqId={}]: {}", reqId, ex.getMessage(), ex);
        ApiResponse<Void> response = ApiResponse.error(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred while processing your request. Please try again.",
                reqId
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
