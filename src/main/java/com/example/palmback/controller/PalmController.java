package com.example.palmback.controller;

import com.example.palmback.dto.PalmAnalysisResponse;
import com.example.palmback.service.PalmAnalysisService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/palm")
public class PalmController {

    private final PalmAnalysisService palmAnalysisService;

    public PalmController(PalmAnalysisService palmAnalysisService) {
        this.palmAnalysisService = palmAnalysisService;
    }

    @PostMapping(
            value = "/analyze",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public PalmAnalysisResponse analyze(@RequestPart(value = "palm", required = false) MultipartFile palm,
                                        @RequestParam("gender") String gender) {
        if (palm == null || palm.isEmpty()){
            throw new IllegalArgumentException("image is required");
        }
        return palmAnalysisService.analyze(palm,gender);
    }
}