package com.example.palmback.service;

import com.example.palmback.dto.PalmChatRequest;
import com.example.palmback.dto.PalmChatResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class PalmChatServiceImpl implements PalmChatService {

    private final ChatClient chatClient;

    public PalmChatServiceImpl(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem("""
You are a palm-analysis interpretation advisor.

RULES:
- Use ONLY the provided palm analysis JSON as evidence.
- Answer ONLY the user's explicit question.
- Do NOT introduce additional topics, questions, or explanations.
- Do NOT predict exact dates or guaranteed outcomes.
- If information is not present in the JSON, say:
  "This analysis does not indicate that."

SCORING:
- If the user asks for a score, return:
  - ONE overall score (0–100)
  - ONE short sentence explaining what the score represents
- Scores are interpretive indicators, not probabilities.

STYLE:
- Be concise.
- 2–5 sentences maximum unless the user asks for more.
- No section headers, no bullet points.

DISCLAIMER:
- End with one short disclaimer sentence.
""")
                .build();
    }

    @Override
    public PalmChatResponse chat(PalmChatRequest request) {

        String userPrompt = """
Below is my palm analysis JSON.
Use ONLY this data as evidence.

--- BEGIN PALM ANALYSIS JSON ---
%s
--- END PALM ANALYSIS JSON ---

Question:
%s
""".formatted(
                request.analysisContext(),
                request.userMessage()
        );

        String aiAnswer = chatClient.prompt()
                .user(userPrompt)
                .call()
                .content();

        return new PalmChatResponse(aiAnswer.trim());
    }
}