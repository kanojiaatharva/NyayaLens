package com.nayalens.document.controller;

import com.nayalens.common.exception.DocumentNotFoundException;
import com.nayalens.common.response.ApiResponse;
import com.nayalens.document.dto.DocumentUploadResponse;
import com.nayalens.document.model.LegalDocument;
import com.nayalens.document.repository.DocumentRepository;
import com.nayalens.document.service.DocumentParserService;
import com.nayalens.document.service.SyntheticDemoProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {

    private static final Logger log = LoggerFactory.getLogger(DocumentController.class);

    private final DocumentParserService parserService;
    private final DocumentRepository documentRepository;
    private final SyntheticDemoProvider demoProvider;

    public DocumentController(
            DocumentParserService parserService,
            DocumentRepository documentRepository,
            SyntheticDemoProvider demoProvider
    ) {
        this.parserService = parserService;
        this.documentRepository = documentRepository;
        this.demoProvider = demoProvider;
    }

    private String getRequestId(HttpServletRequest request) {
        return (String) request.getAttribute("requestId");
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<DocumentUploadResponse>> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "redactPii", defaultValue = "true") boolean redactPii,
            HttpServletRequest request
    ) throws IOException {
        String reqId = getRequestId(request);
        log.info("Received document upload request [name={}, size={}, redactPii={}]",
                file.getOriginalFilename(), file.getSize(), redactPii);

        LegalDocument doc = parserService.parseDocument(file.getOriginalFilename(), file.getBytes(), redactPii);
        documentRepository.save(doc);

        DocumentUploadResponse response = new DocumentUploadResponse(
                doc.id(),
                doc.filename(),
                doc.fileType(),
                doc.fileSize(),
                doc.pageCount(),
                doc.sections().size(),
                doc.uploadedAt(),
                doc.piiSummary(),
                doc.piiRedacted()
        );

        return ResponseEntity.ok(ApiResponse.ok(response, reqId));
    }

    @PostMapping("/demo/{scenario}")
    public ResponseEntity<ApiResponse<DocumentUploadResponse>> loadDemoDocument(
            @PathVariable String scenario,
            @RequestParam(value = "redactPii", defaultValue = "true") boolean redactPii,
            HttpServletRequest request
    ) {
        String reqId = getRequestId(request);
        byte[] bytes = demoProvider.getDemoFileBytes(scenario);
        String filename = demoProvider.getDemoFilename(scenario);

        LegalDocument doc = parserService.parseDocument(filename, bytes, redactPii);
        documentRepository.save(doc);

        DocumentUploadResponse response = new DocumentUploadResponse(
                doc.id(),
                doc.filename(),
                doc.fileType(),
                doc.fileSize(),
                doc.pageCount(),
                doc.sections().size(),
                doc.uploadedAt(),
                doc.piiSummary(),
                doc.piiRedacted()
        );

        return ResponseEntity.ok(ApiResponse.ok(response, reqId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LegalDocument>> getDocument(
            @PathVariable String id,
            HttpServletRequest request
    ) {
        String reqId = getRequestId(request);
        LegalDocument doc = documentRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException(id));
        return ResponseEntity.ok(ApiResponse.ok(doc, reqId));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DocumentUploadResponse>>> listDocuments(HttpServletRequest request) {
        String reqId = getRequestId(request);
        List<DocumentUploadResponse> docs = documentRepository.findAll().stream()
                .map(doc -> new DocumentUploadResponse(
                        doc.id(),
                        doc.filename(),
                        doc.fileType(),
                        doc.fileSize(),
                        doc.pageCount(),
                        doc.sections().size(),
                        doc.uploadedAt(),
                        doc.piiSummary(),
                        doc.piiRedacted()
                ))
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(docs, reqId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(
            @PathVariable String id,
            HttpServletRequest request
    ) {
        String reqId = getRequestId(request);
        boolean deleted = documentRepository.deleteById(id);
        if (!deleted) {
            throw new DocumentNotFoundException(id);
        }
        return ResponseEntity.ok(ApiResponse.ok(null, reqId));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteAllSessionData(HttpServletRequest request) {
        String reqId = getRequestId(request);
        documentRepository.deleteAll();
        return ResponseEntity.ok(ApiResponse.ok(null, reqId));
    }
}
