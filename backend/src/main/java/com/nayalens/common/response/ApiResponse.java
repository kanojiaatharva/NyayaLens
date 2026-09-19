package com.nayalens.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
    boolean success,
    T data,
    ErrorDetails error,
    Instant timestamp,
    String requestId
) {
    public static <T> ApiResponse<T> ok(T data, String requestId) {
        return new ApiResponse<>(true, data, null, Instant.now(), requestId);
    }

    public static <T> ApiResponse<T> error(int status, String code, String message, String requestId) {
        return new ApiResponse<>(false, null, new ErrorDetails(status, code, message), Instant.now(), requestId);
    }

    public record ErrorDetails(
        int status,
        String code,
        String message
    ) {}
}
