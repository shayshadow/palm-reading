package com.example.palmback.dto;

import java.util.List;

public record PalmAnalysisResponse(
        String status,
        String mysticSummary,
        Lines lines,
        DestinyMatch destinyMatch,
        LuckyContext luckyContext
) {
    public record Lines(
            Line lifeLine,
            Line heartLine,
            Line headLine,
            Line fateLine
    ) {}

    public record LuckyContext(
            String luckyColor,    // 행운의 색 (예: Deep Purple)
            String luckyNumber,   // 행운의 숫자 (예: 7)
            String powerAnimal,   // 수호 동물 (예: Owl)
            String actionItem     // 구체적 행동 지침 (예: "Wear gold accessories today")
    ) {}

    public record Line(String description, List<String> traits) {}

    public record DestinyMatch(String archetype, String meaning, double confidence) {}
}