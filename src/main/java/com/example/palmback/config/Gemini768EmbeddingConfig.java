package com.example.palmback.config;

import com.google.genai.Client;
import com.google.genai.types.ContentEmbedding;
import com.google.genai.types.EmbedContentConfig;
import com.google.genai.types.EmbedContentResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class Gemini768EmbeddingConfig {

    private static final String MODEL = "gemini-embedding-001";
    private static final int DIMS = 768;

    @Bean
    public Client genAiClient() {
        String apiKey = System.getenv("GEMINI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("GEMINI_API_KEY is not set");
        }
        return Client.builder().apiKey(apiKey).build();
    }

    @Bean
    public EmbeddingModel embeddingModel(Client client) {
        return new EmbeddingModel() {

            private EmbedContentConfig config() {
                return EmbedContentConfig.builder()
                        .outputDimensionality(DIMS) // ✅ 3072 -> 768 강제
                        .build();
            }

            @Override
            public float[] embed(Document document) {
                String text = document.getText(); // ✅ Spring AI Document는 getText()
                EmbedContentResponse res = client.models.embedContent(MODEL, text, config());

                List<ContentEmbedding> embs = res.embeddings()
                        .orElseThrow(() -> new IllegalStateException("No embeddings() in response"));

                ContentEmbedding emb = embs.get(0);

                List<Float> values = emb.values()
                        .orElseThrow(() -> new IllegalStateException("No values() in embedding"));

                float[] vec = new float[values.size()];
                for (int i = 0; i < values.size(); i++) vec[i] = values.get(i);
                return vec;
            }

            @Override
            public EmbeddingResponse call(EmbeddingRequest request) {
                List<String> inputs = request.getInstructions(); // Spring AI 표준  [oai_citation:3‡Home](https://docs.spring.io/spring-ai/docs/current/api/org/springframework/ai/embedding/EmbeddingRequest.html?utm_source=chatgpt.com)

                // ✅ 입력 정리(공백 제거)
                List<String> cleaned = inputs.stream()
                        .filter(s -> s != null && !s.trim().isBlank())
                        .map(String::trim)
                        .toList();

                if (cleaned.isEmpty()) {
                    throw new IllegalArgumentException("EmbeddingRequest inputs are empty/blank after cleaning");
                }

                // ✅ google genai list embedContent  [oai_citation:4‡Tessl](https://tessl.io/registry/tessl/maven-com-google-genai--google-genai/1.28.0/docs/embeddings-tokens.md?utm_source=chatgpt.com)
                var res = client.models.embedContent(MODEL, cleaned, config());

                var optEmbeddings = res.embeddings();
                if (optEmbeddings.isEmpty() || optEmbeddings.get().isEmpty()) {
                    throw new IllegalStateException("Gemini returned empty embeddings for inputs size=" + cleaned.size());
                }

                var embeddings = optEmbeddings.get();

                // ✅ 사이즈 불일치도 바로 잡기 (원인 추적 쉬움)
                if (embeddings.size() != cleaned.size()) {
                    throw new IllegalStateException("Embeddings size mismatch. inputs=" + cleaned.size() +
                            ", outputs=" + embeddings.size());
                }

                List<Embedding> out = new ArrayList<>(embeddings.size());
                for (int idx = 0; idx < embeddings.size(); idx++) {
                    var optVals = embeddings.get(idx).values();
                    if (optVals.isEmpty() || optVals.get().isEmpty()) {
                        throw new IllegalStateException("No values() for index " + idx);
                    }
                    List<Float> values = optVals.get();
                    float[] vec = new float[values.size()];
                    for (int i = 0; i < values.size(); i++) vec[i] = values.get(i);
                    out.add(new Embedding(vec, idx));
                }
                return new EmbeddingResponse(out);
            }

            @Override
            public int dimensions() {
                return DIMS; // ✅ 원격 호출 안 하고 고정값 반환
            }
        };
    }
}