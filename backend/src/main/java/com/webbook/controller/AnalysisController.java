package com.webbook.controller;

import com.webbook.dto.AnalysisPageDTO;
import com.webbook.service.AnalysisPageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class AnalysisController {

    private final AnalysisPageService analysisPageService;

    public AnalysisController(AnalysisPageService analysisPageService) {
        this.analysisPageService = analysisPageService;
    }

    @GetMapping("/{bookId}/analysis-page")
    public ResponseEntity<AnalysisPageDTO> getAnalysisPage(@PathVariable String bookId) {
        return ResponseEntity.ok(analysisPageService.getAnalysisPage(bookId));
    }
}
