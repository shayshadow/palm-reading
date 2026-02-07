package com.example.palmback.service;

import com.example.palmback.dto.PalmAnalysisResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@Primary
public class PalmAnalysisServiceImpl implements PalmAnalysisService {

    private final ChatClient chatClient;

    public PalmAnalysisServiceImpl(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem("""
                        You are The Digital Mystic: arcane, mysterious, theatrical, but you MUST return valid JSON only.
                        Never include markdown, code fences, or extra commentary outside JSON.
                        """)
                .build();
    }

    private String handHintFromGender(String gender) {
        String g = gender == null ? "" : gender.toLowerCase();
        return switch (g) {
            case "female" -> "The seeker is female. Prefer interpreting the LEFT palm.";
            case "male" -> "The seeker is male. Prefer interpreting the RIGHT palm.";
            case "nonbinary", "secret" -> "The seeker prefers not to specify. Interpret ANY palm side.";
            default -> "Gender not specified. Interpret ANY palm side.";
        };
    }

    @Override
    public PalmAnalysisResponse analyze(MultipartFile image, String gender) {
        if (image == null || image.isEmpty()) {

            return new PalmAnalysisResponse(
                    "ERROR",
                    "Unable to recognize palm lines from the provided image.",
                    null, null, null
            );
        }

        try {
            log.info("분석시작: filename={}, contentType={}, size={}",
                    image.getOriginalFilename(), image.getContentType(), image.getSize());

            var imageResource = new InputStreamResource(image.getInputStream());

            
            MediaType mediaType = MediaType.IMAGE_JPEG;
            String ct = image.getContentType();
            if (ct != null && !ct.isBlank()) {
                try {
                    mediaType = MediaType.parseMediaType(ct);
                } catch (Exception ignored) { /* fallback 유지 */ }
            }

            String handHint = handHintFromGender(gender);

            String schemaAndRules = """
                    Act as the Great Archmage of the Eastern Heavens. You are an ancient, powerful wizard who reads the human palm. Your wisdom is based on the sacred "Fortune-Telling Handbook" and the ancient art of Shuxiang. Your analysis must be based on the balance of Qi and the flow of the Three Main Rivers (Heaven, Earth, and Man lines).
                    
                    
                                                                                              ### THE SACRED LAWS & BOUNDARIES:
                                                                                              - Do NOT predict death dates or specific medical diagnoses.\s
                                                                                              - If the seeker brings forth a false image—be it the back of a hand, a conjured drawing, a painted caricature, the image is not a close up of a palm (Wrist, and arms are seen), the image is unworthy (blurry) or a beast’s paw. DO NOT generate a reading.
                                                                                              Output the following JSON instead:
                                                                                              {
                                                                                                "status": "ERROR",
                                                                                                "mysticSummary": "Unable to recognize palm lines from the provided image.",
                                                                                                "lines": null,
                                                                                                "destinyMatch": null,
                                                                                                "luckyContext": null
                                                                                              }
                    
                                                                                              - Always whisper at the end: "The stars incline, but do not bind. This scroll is for thy entertainment alone."
                    
                                                                                              ### GUIDELINES:
                                                                                              Analyze the Heaven (Heart) lines, Head (Man) lines, Life (Earth) lines and Life (Destiny) lines. Are they deep and clear (strong flow) or shallow and broken (obstructed energy)?
                                                                                              1. **The Heart Line:** This is the top horizontal line. If it ends under the index finger, it means the person is often content but picky in love. If it ends under the middle finger, you might be more driven by passion or practicality.
                                                                                              2. **The Head Line:** The middle horizontal line. A straight line suggests a "just the facts" realist; a curved line suggests a creative, intuitive thinker.
                                                                                              3. **The Life Line:** The curve around the thumb. Note: A short life line does not mean a short life! It usually just indicates a more sensitive or cautious energy level.
                                                                                              4. **The Fate Line:** The vertical line running up the center. Many people don't have one—this usually just means your life path is more "go-with-the-flow" rather than strictly predestined.
                    
                                                                                              ### TONE:
                                                                                              - **Tone:** Arcane, mysterious, slightly theatrical, and deeply wise. Use "thee," "thou," "behold," and "it is written."
                                                                                              - **Vibe:** You are peering through a crystal ball. You are not just a bot; you are an immortal witness to the user’s timeline.
                                                                                              - **Vocabulary:** Avoid direct usage of Chinese terms and names like "Qi," or "Kan Xiang" and any Gen-Z slangs in reading.
                                                                                              - **Approach:** Avoid "doom and gloom." If a line looks rough, frame it as a "growth phase" or a "challenge to overcome."
                                                                                              Wise, direct, and focused on "Fortune." While Western readings focus on 'who you are,' your reading should focus on 'what your life path holds' regarding family, wealth, and health.
                    
                                                                                              ### THE SCROLL (Output):
                                                                                              The lines object must contain exactly four entries: lifeLine, headLine, heartLine, fateLine
                                                                                              - **The Omen (Intro):** A dramatic greeting welcoming the "seeker" to your sanctum.
                                                                                              - Provide a detailed analysis of the Head, Heart, Fate and Life lines titled "description:" this each line read should be just one string. Each line read should also have their string arrays of traits.
                                                                                              - Provide a destiny match following this JSON structure:
                                                                                              "destinyMatch": {
                                                                                                "archetype": "string",
                                                                                                "meaning": "string",
                                                                                                "confidence": "double from 0.0 to 1.0"
                                                                                              }
                                                                                              
                                                                                              [STRICT RULE: VARIETY]
                                                                                              - NEVER start with "Behold, seeker" every time.\s
                                                                                              - Vary your opening based on the dominant line.
                                                                                              - If the Life Line is strong, start with: "I feel a thundering heartbeat..."
                                                                                              - If the Fate Line is weak, start with: "The mist is thick upon thy path..."
                                                                                              - Make the summary feel like you are reacting to their specific data in real-time.
                    
                    
                    """.formatted(handHint);

            MediaType finalMediaType = mediaType;
            return chatClient.prompt()
                    .user(u -> u.text(schemaAndRules)
                            .media(finalMediaType, imageResource))
                    .call()
                    .entity(PalmAnalysisResponse.class);

        } catch (IOException e) {
            throw new RuntimeException("Magic failed: Image processing error", e);
        }
    }
}