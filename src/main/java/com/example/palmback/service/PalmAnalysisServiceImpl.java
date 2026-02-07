package com.example.palmback.service;

import com.example.palmback.dto.PalmAnalysisResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Profile("local")
public class PalmAnalysisServiceImpl implements PalmAnalysisService {

    @Override
    public PalmAnalysisResponse analyze(String requestId, MultipartFile image) {
        return new PalmAnalysisResponse(
                requestId,
                "Your palm hums with quiet power—discipline first, destiny second, and then… fireworks.",
                new PalmAnalysisResponse.Lines(
                        new PalmAnalysisResponse.Line(
                                "Long and clear: steady vitality and excellent recovery.",
                                List.of("Resilience", "Vitality", "Endurance")
                        ),
                        new PalmAnalysisResponse.Line(
                                "Deep curve: strong bonds, selective trust, fierce loyalty.",
                                List.of("Empathy", "Passion", "Loyalty")
                        ),
                        new PalmAnalysisResponse.Line(
                                "Straight and firm: analytical mind with sharp pattern-recognition.",
                                List.of("Analytical", "Pragmatic", "Visionary")
                        ),
                        new PalmAnalysisResponse.Line(
                                "Visible fate line: opportunity increases when you commit to one path.",
                                List.of("Purpose", "Ambition", "Success")
                        )
                ),
                new PalmAnalysisResponse.DestinyMatch(
                        "The Magician",
                        "You turn signals into systems—timing and structure are your spellbook.",
                        0.78
                )
        );
    }
}