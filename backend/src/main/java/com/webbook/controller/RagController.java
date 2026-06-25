package com.webbook.controller;

import com.webbook.dto.RagConfigDTO;
import com.webbook.dto.RagStatusDTO;
import com.webbook.dto.SourceRecordDTO;
import com.webbook.service.RagIndexService;
import com.webbook.service.RAGService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final RagIndexService ragIndexService;
    private final RAGService ragService;

    public RagController(RagIndexService ragIndexService, RAGService ragService) {
        this.ragIndexService = ragIndexService;
        this.ragService = ragService;
    }

    // RAG Status
    @GetMapping("/status/{bookId}")
    public ResponseEntity<RagStatusDTO> getStatus(@PathVariable String bookId) {
        return ResponseEntity.ok(ragIndexService.getStatus(bookId));
    }

    // Build index
    @PostMapping("/index/{bookId}")
    public ResponseEntity<RagStatusDTO> buildIndex(@PathVariable String bookId,
                                                    @RequestBody(required = false) RagConfigDTO config) {
        int chunkSize = config != null && config.getChunkSize() != null ? config.getChunkSize() : 500;
        int overlap = config != null && config.getOverlap() != null ? config.getOverlap() : 100;
        return ResponseEntity.ok(ragIndexService.buildIndex(bookId, chunkSize, overlap));
    }

    // Rebuild index
    @PutMapping("/reindex/{bookId}")
    public ResponseEntity<RagStatusDTO> rebuildIndex(@PathVariable String bookId,
                                                      @RequestBody(required = false) RagConfigDTO config) {
        int chunkSize = config != null && config.getChunkSize() != null ? config.getChunkSize() : 500;
        int overlap = config != null && config.getOverlap() != null ? config.getOverlap() : 100;
        return ResponseEntity.ok(ragIndexService.rebuildIndex(bookId, chunkSize, overlap));
    }

    // Clear index
    @DeleteMapping("/index/{bookId}")
    public ResponseEntity<Void> clearIndex(@PathVariable String bookId) {
        ragIndexService.clearIndex(bookId);
        return ResponseEntity.ok().build();
    }

    // Get RAG config
    @GetMapping("/config/{bookId}")
    public ResponseEntity<RagConfigDTO> getConfig(@PathVariable String bookId) {
        return ResponseEntity.ok(ragIndexService.getConfig(bookId));
    }

    // Update RAG config
    @PutMapping("/config/{bookId}")
    public ResponseEntity<Void> updateConfig(@PathVariable String bookId, @RequestBody RagConfigDTO config) {
        ragIndexService.updateConfig(bookId, config);
        return ResponseEntity.ok().build();
    }

    // Global cache cleanup
    @DeleteMapping("/cache/global")
    public ResponseEntity<Void> clearGlobalCache() {
        ragIndexService.clearAllCache();
        return ResponseEntity.ok().build();
    }

    // RAG search (for testing)
    @PostMapping("/search/{bookId}")
    public ResponseEntity<List<SourceRecordDTO>> search(@PathVariable String bookId, @RequestBody Map<String, String> body) {
        String query = body.get("query");
        return ResponseEntity.ok(ragService.searchWithSources(bookId, query));
    }

    // Get all built statuses
    @GetMapping("/status")
    public ResponseEntity<List<RagStatusDTO>> getAllStatus() {
        return ResponseEntity.ok(ragService.getAllBuiltStatus());
    }
}
