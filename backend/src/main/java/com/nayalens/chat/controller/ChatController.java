package com.nayalens.chat.controller;

import com.nayalens.chat.dto.ChatAnswerResponse;
import com.nayalens.chat.dto.ChatQuestionRequest;
import com.nayalens.chat.service.DocumentChatService;
import com.nayalens.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/questions")
public class ChatController {

    private final DocumentChatService chatService;

    public ChatController(DocumentChatService chatService) {
        this.chatService = chatService;
    }

    private String getRequestId(HttpServletRequest request) {
        return (String) request.getAttribute("requestId");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ChatAnswerResponse>> askQuestion(
            @Valid @RequestBody ChatQuestionRequest requestDto,
            HttpServletRequest request
    ) {
        String reqId = getRequestId(request);
        ChatAnswerResponse answer = chatService.answerQuestion(requestDto);
        return ResponseEntity.ok(ApiResponse.ok(answer, reqId));
    }
}
