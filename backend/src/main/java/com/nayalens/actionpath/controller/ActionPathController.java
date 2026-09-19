package com.nayalens.actionpath.controller;

import com.nayalens.actionpath.dto.ActionPathRequest;
import com.nayalens.actionpath.model.ActionPathResult;
import com.nayalens.actionpath.service.ActionPathService;
import com.nayalens.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/actionpath")
public class ActionPathController {

    private final ActionPathService actionPathService;

    public ActionPathController(ActionPathService actionPathService) {
        this.actionPathService = actionPathService;
    }

    private String getRequestId(HttpServletRequest request) {
        return (String) request.getAttribute("requestId");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ActionPathResult>> getActionPath(
            @Valid @RequestBody ActionPathRequest requestDto,
            HttpServletRequest request
    ) {
        String reqId = getRequestId(request);
        ActionPathResult result = actionPathService.generateActionPath(requestDto.documentId());
        return ResponseEntity.ok(ApiResponse.ok(result, reqId));
    }
}
