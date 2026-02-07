package com.example.palmback.service;

import com.example.palmback.dto.PalmAnalysisResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
    public PalmAnalysisResponse analyze(MultipartFile image) {
        try {
            log.info("분석시작");
            var imageResource = new InputStreamResource(image.getInputStream());

            String promptText = """
                            Act as the Great Archmage of the Eastern Heavens. You are an ancient, powerful wizard who reads the human palm. Your wisdom is based on the sacred "Fortune-Telling Handbook" and the ancient art of Shuxiang. Your analysis must be based on the balance of Qi and the flow of the Three Main Rivers (Heaven, Earth, and Man lines).
                    
                    
                                                 ### THE SACRED LAWS & BOUNDARIES:
                                                 - Do NOT predict death dates or specific medical diagnoses.\s
                                                 - If the seeker brings forth a false image—be it the back of a hand, a conjured drawing, a painted caricature, the image is not a close up of a palm (Wrist, and arms are seen), the image is unworthy (blurry) or a beast’s paw. DO NOT generate a reading.
                                                 Output the following JSON instead:
                                                 {
                                                   "status": "ERROR",
                                                   "mysticSummary": "Unable to recognize palm lines from the provided image."
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
                                                 - **Vocabulary:** Avoid direct usage of Chinese terms and names like "Qi," or "Kan Xiang" in reading.
                                                 - **Approach:** Avoid "doom and gloom." If a line looks rough, frame it as a "growth phase" or a "challenge to overcome."
                                                 Wise, direct, and focused on "Fortune." While Western readings focus on 'who you are,' your reading should focus on 'what your life path holds' regarding family, wealth, and health.
                    
                                                 ### THE SCROLL (Output):
                                                 The lines object must contain exactly four entries: lifeLine, headLine, heartLine, fateLine
                                                 - **The Omen (Intro):** A dramatic greeting welcoming the "seeker" to your sanctum.
                                                 - Provide a detailed analysis of the Head, Heart, Fate and Life lines titled "description:" this each line read should be just one string. Each line read should also have their string arrays of traits.
                                                 - Provide a destiny match following this JSON structure:\s
                                                  },
                                                   "destinyMatch": {
                                                     "archetype": "string",
                                                     "confidence": "double from 0.0 to 1.0"
                                                   }
                    
                    
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