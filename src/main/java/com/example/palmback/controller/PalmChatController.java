package com.example.palmback.controller;


import com.example.palmback.dto.PalmChatRequest;
import com.example.palmback.dto.PalmChatResponse;
import com.example.palmback.service.PalmChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/palm")
public class PalmChatController {

    private final PalmChatService palmChatService;

    public PalmChatController(PalmChatService palmChatService) {
        this.palmChatService = palmChatService;
    }

    @PostMapping("/chat")
    public ResponseEntity<PalmChatResponse> chat(@RequestBody PalmChatRequest request) {
        // 1. 유효성 검사 (질문 없거나 컨텍스트 없으면 에러)
        if (request.userMessage() == null || request.userMessage().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new PalmChatResponse("질문을 입력해주게, 어린 양이여."));
        }

        // 2. 서비스 호출
        PalmChatResponse response = palmChatService.chat(request);
        return ResponseEntity.ok(response);
    }
}
