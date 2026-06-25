package com.webbook.controller;

import com.webbook.entity.Chapter;
import com.webbook.service.ChapterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chapters")
public class ChapterController {

    private final ChapterService chapterService;

    public ChapterController(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    @GetMapping("/{id}/content")
    public ResponseEntity<Map<String, String>> getChapterContent(@PathVariable Long id) {
        return chapterService.findById(id)
                .map(ch -> {
                    String content = chapterService.readChapterContent(ch);
                    return ResponseEntity.ok(Map.of(
                            "bookId", ch.getBookId(),
                            "chapterIndex", String.valueOf(ch.getChapterIndex()),
                            "chapterTitle", ch.getChapterTitle() != null ? ch.getChapterTitle() : "",
                            "content", content
                    ));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
