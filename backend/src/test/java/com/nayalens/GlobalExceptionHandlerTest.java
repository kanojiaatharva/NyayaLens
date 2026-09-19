package com.nayalens;

import com.nayalens.common.exception.DocumentNotFoundException;
import com.nayalens.common.exception.GlobalExceptionHandler;
import com.nayalens.common.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest();
        request.setAttribute("requestId", "req_test_123");
    }

    @Test
    @DisplayName("Should return HTTP 404 envelope when DocumentNotFoundException is thrown")
    void testHandleDocumentNotFound() {
        DocumentNotFoundException ex = new DocumentNotFoundException("doc_not_found_1");
        ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleApiException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().success());
        assertEquals("DOCUMENT_NOT_FOUND", response.getBody().error().code());
        assertEquals("req_test_123", response.getBody().requestId());
    }

    @Test
    @DisplayName("Should return HTTP 400 envelope when IllegalArgumentException is thrown")
    void testHandleIllegalArgument() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid parameter value");
        ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleIllegalArgument(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().success());
        assertEquals("INVALID_ARGUMENT", response.getBody().error().code());
    }

    @Test
    @DisplayName("Should return HTTP 500 envelope for general unexpected exception")
    void testHandleGeneralException() {
        RuntimeException ex = new RuntimeException("Unexpected runtime error");
        ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleGeneralException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().success());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().error().code());
    }
}
