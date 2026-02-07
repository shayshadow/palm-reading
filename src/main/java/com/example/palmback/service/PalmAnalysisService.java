package com.example.palmback.service;

import com.example.palmback.dto.PalmAnalysisResponse;
import org.springframework.web.multipart.MultipartFile;

public interface PalmAnalysisService {

    PalmAnalysisResponse analyze( MultipartFile image);

}
