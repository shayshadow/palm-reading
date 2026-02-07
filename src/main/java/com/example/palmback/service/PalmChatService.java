package com.example.palmback.service;

import com.example.palmback.dto.PalmChatRequest;
import com.example.palmback.dto.PalmChatResponse;

public interface PalmChatService {
    PalmChatResponse chat(PalmChatRequest request);
}
