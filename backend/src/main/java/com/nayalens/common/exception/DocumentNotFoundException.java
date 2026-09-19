package com.nayalens.common.exception;

import org.springframework.http.HttpStatus;

public class DocumentNotFoundException extends ApiException {
    public DocumentNotFoundException(String documentId) {
        super(HttpStatus.NOT_FOUND, "DOCUMENT_NOT_FOUND", "Document not found or expired: " + documentId);
    }
}
