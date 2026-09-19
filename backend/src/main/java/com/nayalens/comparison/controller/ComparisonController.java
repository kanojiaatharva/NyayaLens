package com.nayalens.comparison.controller;

import com.nayalens.common.response.ApiResponse;
import com.nayalens.comparison.dto.ComparisonRequest;
import com.nayalens.comparison.model.ComparisonResult;
import com.nayalens.comparison.service.ComparisonService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/compare")
public class ComparisonController {

    private final ComparisonService comparisonService;

    public ComparisonController(ComparisonService comparisonService) {
        this.comparisonService = comparisonService;
    }

    private String getRequestId(HttpServletRequest request) {
        return (String) request.getAttribute("requestId");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ComparisonResult>> compareDocuments(
            @Valid @RequestBody ComparisonRequest requestDto,
            HttpServletRequest request
    ) {
        String reqId = getRequestId(request);
        ComparisonResult result = comparisonService.compareDocuments(requestDto.documentAId(), requestDto.documentBId());
        return ResponseEntity.ok(ApiResponse.ok(result, reqId));
    }
}
