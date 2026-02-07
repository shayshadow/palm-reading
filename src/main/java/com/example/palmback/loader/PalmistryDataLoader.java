package com.example.palmback.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class PalmistryDataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PalmistryDataLoader.class);
    private final VectorStore vectorStore;
    private final EmbeddingModel embeddingModel;

    @Value("classpath:palm-data/part1.txt") // 폴더 구조에 맞게 경로 확인!
    private Resource part1;

    @Value("classpath:palm-data/part2.txt")
    private Resource part2;

    @Value("classpath:palm-data/part3.txt")
    private Resource part3;

    public PalmistryDataLoader(VectorStore vectorStore,EmbeddingModel embeddingModel) {
        this.vectorStore = vectorStore;
        this.embeddingModel = embeddingModel;
    }

    @Override
    public void run(String... args) {
        log.info("🔮 The Digital Mystic: 고대 지식을 천천히 불러옵니다...");

        try {
            List<Document> allDocuments = new ArrayList<>();

            // 1. 모든 데이터 일단 메모리에 로드
            allDocuments.addAll(loadAndChunk(part1, "Part 1 - Introduction & Shape"));
            allDocuments.addAll(loadAndChunk(part2, "Part 2 - The Mounts"));
            allDocuments.addAll(loadAndChunk(part3, "Part 3 - The Lines"));

            if (allDocuments.isEmpty()) {
                log.warn("⚠️ 로드할 데이터가 없습니다.");
                return;
            }

            // ✅ 임베딩 차원 사전 체크: 첫 조각 한 개만 임베딩해서 차원 확인
            Document d0 = allDocuments.get(0);
            int dims = embeddingModel.embed(d0).length;
            log.info("✅ embedding dims = {}", dims);

            // ✅ HNSW + pgvector(vector) 제한(<=2000) 위반이면 여기서 즉시 중단
            if (dims > 2000) {
                throw new IllegalStateException(
                        "Embedding dimension is " + dims +
                                " (>2000). pgvector HNSW index cannot be created for vector type. " +
                                "Fix embedding dimensionality (e.g., 768/1536) or change storage type.");
            }

            log.info("총 {}개의 지식 조각을 발견했습니다. API 제한을 피해 나눠서 저장합니다.", allDocuments.size());

            // 2. [Rate Limit 회피] 배치 처리 (10개씩 끊어서 저장 + 휴식)
            int batchSize = 10; // 한 번에 보낼 개수 (안전하게 10개)
            for (int i = 0; i < allDocuments.size(); i += batchSize) {
                int end = Math.min(i + batchSize, allDocuments.size());
                List<Document> batch = allDocuments.subList(i, end);

                try {

                    List<Document> safeBatch = batch.stream()
                            .filter(d -> d.getText() != null && !d.getText().trim().isBlank())
                            .filter(d -> d.getText().trim().length() >= 10)
                            .toList();

                    if (safeBatch.isEmpty()) {
                        log.warn("⚠️ 이번 배치는 모두 빈/짧은 청크라 스킵합니다. ({}~{})", i, end);
                        continue;
                    }
                    vectorStore.add(safeBatch);
                    log.info("✅ 저장 중... ({}/{})", end, allDocuments.size());

                    // 🚨 중요: 구글 API 형님이 화내지 않게 2초 쉽니다.
                    Thread.sleep(2000);

                } catch (Exception e) {
                    log.error("❌ 배치 저장 중 오류 발생 (무시하고 계속 진행): ", e);
                    // 429가 또 뜨면 조금 더 오래 쉬게 설정
                    Thread.sleep(5000);
                }
            }

            log.info("✨ 모든 지식 주입 완료! 이제 점을 칠 수 있습니다! 🔮");

        } catch (Exception e) {
            log.error("❌ 지식 로딩 치명적 오류: ", e);
        }
    }

    private List<Document> loadAndChunk(Resource resource, String sourceTitle) throws IOException {
        if (!resource.exists()) return List.of();

        String content = new String(resource.getContentAsByteArray(), StandardCharsets.UTF_8);
        Map<String, Object> metadata = Map.of("source", sourceTitle, "category", "Scientific Palmistry");
        Document rawDoc = new Document(content, metadata);

        // 청킹 설정 (아까랑 동일)
        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(1000)
                .withMinChunkSizeChars(200)
                .withMinChunkLengthToEmbed(10)
                .withMaxNumChunks(10000)
                .withKeepSeparator(true)
                .build();

        List<Document> chunks = splitter.apply(List.of(rawDoc));

        // ✅ 핵심: 공백/너무 짧은 청크 제거 (이거 없으면 embeddings=0 나올 수 있음)
        return chunks.stream()
                .filter(d -> d.getText() != null)
                .map(d -> new Document(d.getText().trim(), d.getMetadata()))
                .filter(d -> !d.getText().isBlank())
                .filter(d -> d.getText().length() >= 10)  // 안전장치(원하는 기준으로 조정)
                .toList();
    }
}