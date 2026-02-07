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
    private final VectorStore vectorStore;

    public PalmAnalysisServiceImpl(ChatClient.Builder builder, VectorStore vectorStore) {
        this.chatClient = builder
                .defaultSystem("You are 'The Digital Mystic', a wise palmist. " +
                        "Analyze the user's palm image and provide a mystical yet structured reading.")
                .build();
        this.vectorStore = vectorStore;
    }

    @Override
    public PalmAnalysisResponse analyze(String requestId, MultipartFile image) {
        try {
            var imageResource = new InputStreamResource(image.getInputStream());

            // 1단계: VLM을 통한 이미지 분석 및 특징 추출
            String visualTraits = chatClient.prompt()
                    .user(u -> u.text("Describe the Life Line, Heart Line, and Head Line from this palm.")
                            .media(MimeTypeUtils.IMAGE_JPEG, imageResource))
                    .call()
                    .content();

            // 2단계: RAG - 추출된 특징으로 고대 문헌 검색
            SearchRequest request = SearchRequest.builder()
                    .query(visualTraits)
                    .topK(2)
                    .build();

            List<Document> similarDocs = vectorStore.similaritySearch(request);

            String context = similarDocs.stream()
                    .map(Document::getText)
                    .collect(Collectors.joining("\n"));

            // 3단계: LLM - 특징 + 문헌 지식을 결합하여 구조화된 JSON 응답 생성
            return chatClient.prompt()
                    .user(u -> u.text("Based on the visual traits: {traits} and ancient wisdom: {context}, " +
                                    "generate a detailed palmistry report in JSON.")
                            .param("traits", visualTraits)
                            .param("context", context))
                    .call()
                    .entity(PalmAnalysisResponse.class); // DTO 구조로 자동 매핑!

        } catch (IOException e) {
            throw new RuntimeException("Magic failed: Image processing error", e);
        }
    }
}