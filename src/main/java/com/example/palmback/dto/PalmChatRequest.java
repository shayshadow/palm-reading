package com.example.palmback.dto;

public record PalmChatRequest(
        String userMessage,
        String analysisContext
) {
}
