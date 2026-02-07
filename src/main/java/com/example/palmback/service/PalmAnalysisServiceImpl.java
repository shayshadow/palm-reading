package com.example.palmback.service;

import com.example.palmback.dto.PalmAnalysisResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Primary;
import org.springframework.ai.document.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
public class PalmAnalysisServiceImpl implements PalmAnalysisService {

    private final ChatClient chatClient;

    public PalmAnalysisServiceImpl(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem("You are 'The Digital Mystic', a wise palmist. " +
                        "Analyze the user's palm image and provide a mystical yet structured reading.")
                .build();
    }

    @Override
    public PalmAnalysisResponse analyze(String requestId, MultipartFile image) {
        try {
            var imageResource = new InputStreamResource(image.getInputStream());

            String promptText = """
            You are an expert Palmist. Perform a step-by-step analysis on this palm image.
            
            [Step 1: Visual Analysis]
            Identify and describe the following visual traits strictly from the image:
            - Life Line: (Length, depth, breaks, curvature)
            - Head Line: (Length, slope, origin)
            - Heart Line: (Curve, branches, islands)
            - Mounts: (Identify which mounts are prominent or padded)
            
            [Step 2: Destiny Reading]
            Based on the traits identified in Step 1, apply ancient palmistry knowledge to predict the user's destiny.
            
            [Output Requirement]
            Ignore the background. Focus only on the palm.
            Provide the final result in the requested JSON format.
            """;


            // 3단계: LLM - 특징 + 문헌 지식을 결합하여 구조화된 JSON 응답 생성
            return chatClient.prompt()
                    .user(u -> u.text(promptText)
                            .media(MimeTypeUtils.IMAGE_JPEG, imageResource))
                    .call()
                    .entity(PalmAnalysisResponse.class);// DTO 구조로 자동 매핑!

        } catch (IOException e) {
            throw new RuntimeException("Magic failed: Image processing error", e);
        }
    }
}