package com.nayalens.legalresources.controller;

import com.nayalens.common.exception.ApiException;
import com.nayalens.common.response.ApiResponse;
import com.nayalens.legalresources.model.LegalResource;
import com.nayalens.legalresources.service.LegalResourceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/legal-resources")
public class LegalResourceController {

    private final LegalResourceService resourceService;

    public LegalResourceController(LegalResourceService resourceService) {
        this.resourceService = resourceService;
    }

    private String getRequestId(HttpServletRequest request) {
        return (String) request.getAttribute("requestId");
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LegalResource>>> listResources(HttpServletRequest request) {
        String reqId = getRequestId(request);
        return ResponseEntity.ok(ApiResponse.ok(resourceService.getAllResources(), reqId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LegalResource>> getResource(@PathVariable String id, HttpServletRequest request) {
        String reqId = getRequestId(request);
        LegalResource res = resourceService.getResourceById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", "Legal resource not found: " + id));
        return ResponseEntity.ok(ApiResponse.ok(res, reqId));
    }
}
