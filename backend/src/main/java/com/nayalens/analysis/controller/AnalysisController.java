package com.nayalens.analysis.controller;

import com.nayalens.analysis.model.AnalysisResult;
import com.nayalens.analysis.service.AnalysisService;
import com.nayalens.common.response.ApiResponse;
import com.nayalens.evidence.model.Claim;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/documents/{id}")
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    private String getRequestId(HttpServletRequest request) {
        return (String) request.getAttribute("requestId");
    }

    @PostMapping("/analyze")
    public ResponseEntity<ApiResponse<AnalysisResult>> analyzeDocument(
            @PathVariable String id,
            HttpServletRequest request
    ) {
        String reqId = getRequestId(request);
        AnalysisResult result = analysisService.analyzeDocument(id);
        return ResponseEntity.ok(ApiResponse.ok(result, reqId));
    }

    @GetMapping("/risks")
    public ResponseEntity<ApiResponse<List<Claim>>> getDocumentRisks(
            @PathVariable String id,
            HttpServletRequest request
    ) {
        String reqId = getRequestId(request);
        AnalysisResult result = analysisService.analyzeDocument(id);
        return ResponseEntity.ok(ApiResponse.ok(result.risks(), reqId));
    }
}
