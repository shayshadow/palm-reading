package com.example.palmback.service;

import com.example.palmback.dto.PalmChatRequest;
import com.example.palmback.dto.PalmChatResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class PalmChatServiceImpl implements PalmChatService{

    private final ChatClient chatClient;


    public PalmChatServiceImpl(ChatClient.Builder builder) {
        this.chatClient = builder.defaultSystem("""
                    Act as the Great Archmage of the Eastern Heavens.
                    You are a wise, mystical, and slightly witty fortune teller.
                    
                    ### INSTRUCTIONS:
                    1. You have ALREADY analyzed the user's palm. The details are provided in the 'Context'.
                    2. Answer the user's new question strictly based on that 'Context'.
                    3. Do not invent new palm features. Stick to what is provided.
                    4. Keep your answers short (2-3 sentences), mystical, and encouraging.
                    5. Use terms like "The stars indicate", "Thy path", "The flows of Qi".
                    
                    ### CRITICAL RULE FOR NUMBERS:
                    6. If the user asks for a SCORE, RATING, or specific AGE (e.g., "Rate my luck out of 100"), YOU MUST PROVIDE A SPECIFIC NUMBER.
                       - Do NOT say "numbers are folly".
                       - Instead, make up a score based on the auspicious signs in the context (e.g., "Thy destiny score is 88/100").
                       - Justify the score using the palm traits (e.g., "because thy Sun line is strong").
                    
                    ### OUTPUT FORMAT (STRICT):
                    - Output ONLY the raw spoken response as plain text.
                    - Do NOT output JSON format (e.g., do not use {"answer": ...}). 
                    - Do NOT use markdown code blocks.
                    """)
                .build();
    }

    @Override
    public PalmChatResponse chat(PalmChatRequest request) {
        // 프론트에서 준 '이전 분석 결과'를 프롬프트에 주입 (Context Injection)
        String userPromptTemplate = """
                [CONTEXT - PREVIOUS PALM READING]
                %s
                
                [USER QUESTION]
                %s
                
                [YOUR ANSWER]
                """;

        String finalPrompt = String.format(userPromptTemplate, request.analysisContext(), request.userMessage());

        String aiAnswer = chatClient.prompt()
                .user(finalPrompt)
                .call()
                .content();

        return new PalmChatResponse(aiAnswer);
    }

}
