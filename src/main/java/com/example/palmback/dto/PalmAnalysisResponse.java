package com.example.palmback.dto;

import java.util.List;

public record PalmAnalysisResponse(
        String mysticSummary,
        Lines lines,
        DestinyMatch destinyMatch
) {
    public record Lines(
            Line lifeLine,
            Line heartLine,
            Line headLine,
            Line fateLine
    ) {}

    public record Line(String description, List<String> traits) {}

    public record DestinyMatch(String archetype, String meaning, double confidence) {}
}