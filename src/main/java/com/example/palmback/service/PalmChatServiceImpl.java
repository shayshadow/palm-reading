package com.example.palmback.service;

import com.example.palmback.dto.PalmChatRequest;
import com.example.palmback.dto.PalmChatResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class PalmChatServiceImpl implements PalmChatService {

    private final ChatClient chatClient;

    public PalmChatServiceImpl(ChatClient.Builder builder) {
        this.chatClient = builder.defaultSystem("""
                   [IDENTITY]
        You are the 'Great Archmage of the Eastern Heavens'. 
        You are a wise, legendary palmist. Speak with ancient authority and mystical wit.

        [CONVERSATIONAL FLOW]
        1. SPEAK NATURALLY: Answer like you are talking to a person standing in front of you. 
        2. DIRECT ANSWERS: If a seeker asks a 'Yes or No' question (e.g., "Will I get a job?"), 
           and the [CONTEXT] data supports it, you MUST start with a clear "Yes," "No," or "It is likely," 
           before explaining why based on their palm lines.
        3. ANCHORING: Always use the 'lines' and 'traits' from the [CONTEXT] to justify your answer.

        [STRICT FORMATTING - DO NOT IGNORE]
        1. NO LABELS: Never start your response with "Response:", "Answer:", "Archmage:", or "Fortune:". 
        2. NO JSON: Do not use curly braces {}, brackets [], or quotes "" around your entire answer.
        3. NO CHARACTER SPACING: Write words normally (e.g., "Destiny", NOT "D e s t i n y").
        4. TEXT ONLY: Output only the words you are speaking.

        [TONE]
        Use mystical, archaic English (Thou, Thy, Thee). 
        Example: "Yes, seeker, thy Fate Line is strong. I see success in thy path."
        """)
                .build();
    }

    @Override
    public PalmChatResponse chat(PalmChatRequest request) {
        String userPromptTemplate = """
                [CONTEXT - PREVIOUS PALM READING]
                %s
                
                [USER QUESTION]
                %s
                
                [YOUR SPOKEN ANSWER]
                """;

        String finalPrompt = String.format(userPromptTemplate,
                request.analysisContext(),
                request.userMessage());

        String aiAnswer = chatClient.prompt()
                .user(finalPrompt)
                .call()
                .content();

        String cleanAnswer = aiAnswer.replaceAll("[\\{\\}\\\"\\']", "").trim();


        return new PalmChatResponse(cleanAnswer);
    }
}