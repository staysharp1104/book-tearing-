package com.webbook.service;

import com.webbook.dto.RagConfigDTO;
import com.webbook.dto.RagStatusDTO;
import com.webbook.entity.RagIndexInfo;
import com.webbook.rag.*;
import com.webbook.repository.RagIndexInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class RagIndexService {
    private static final Logger log = LoggerFactory.getLogger(RagIndexService.class);

    private final RagIndexInfoRepository ragIndexInfoRepository;
    private final RAGIndexer indexer;
    private final RAGCacheManager cacheManager;

    public RagIndexService(RagIndexInfoRepository ragIndexInfoRepository,
                           RAGIndexer indexer,
                           RAGCacheManager cacheManager) {
        this.ragIndexInfoRepository = ragIndexInfoRepository;
        this.indexer = indexer;
        this.cacheManager = cacheManager;
    }

    public RagStatusDTO getStatus(String bookId) {
        Optional<RagIndexInfo> info = ragIndexInfoRepository.findByBookId(bookId);

        if (info.isEmpty()) {
            return new RagStatusDTO(bookId, "not_built", 500, 100, 3, true, 0, 0, null);
        }

        RagIndexInfo i = info.get();
        return new RagStatusDTO(
                bookId, i.getStatus(), i.getChunkSize(), i.getOverlap(),
                i.getTopK(), i.getShortBookFullText(),
                i.getChunkCount(), i.getWordCount(), i.getBuiltAt()
        );
    }

    @Transactional
    public RagStatusDTO buildIndex(String bookId, int chunkSize, int overlap) {
        Optional<RagIndexInfo> existing = ragIndexInfoRepository.findById(bookId);
        if (existing.isPresent() && "built".equals(existing.get().getStatus())) {
            throw new IllegalArgumentException("\u7d22\u5f15\u5df2\u6784\u5efa\uff0c\u5982\u9700\u91cd\u5efa\u8bf7\u4f7f\u7528\u91cd\u5efa\u63a5\u53e3");
        }

        RAGIndex index = indexer.buildIndex(bookId, chunkSize, overlap);

        String cachePath = indexer.getCacheFilePath(bookId);
        try {
            cacheManager.saveIndex(index, cachePath);
        } catch (Exception e) {
            throw new RuntimeException("\u4fdd\u5b58\u7d22\u5f15\u7f13\u5b58\u5931\u8d25: " + e.getMessage());
        }

        RagIndexInfo info = existing.orElseGet(RagIndexInfo::new);
        info.setBookId(bookId);
        info.setStatus("built");
        info.setChunkSize(chunkSize);
        info.setOverlap(overlap);
        info.setChunkCount(index.getChunkTexts().size());
        info.setWordCount((int) index.getTotalWords());
        info.setBuiltAt(LocalDateTime.now());
        info.setCachePath(cachePath);
        info.setUpdatedAt(LocalDateTime.now());
        ragIndexInfoRepository.save(info);

        return getStatus(bookId);
    }

    @Transactional
    public RagStatusDTO rebuildIndex(String bookId, int chunkSize, int overlap) {
        clearIndex(bookId);
        return buildIndex(bookId, chunkSize, overlap);
    }

    @Transactional
    public void clearIndex(String bookId) {
        String cachePath = indexer.getCacheFilePath(bookId);
        cacheManager.deleteIndex(cachePath);

        if (ragIndexInfoRepository.existsById(bookId)) {
            ragIndexInfoRepository.deleteById(bookId);
        }
    }

    @Transactional
    public void clearAllCache() {
        ragIndexInfoRepository.deleteAll();
        log.info("Global RAG cache cleared");
    }

    public RagConfigDTO getConfig(String bookId) {
        Optional<RagIndexInfo> info = ragIndexInfoRepository.findByBookId(bookId);
        RagConfigDTO config = new RagConfigDTO();

        if (info.isPresent()) {
            config.setChunkSize(info.get().getChunkSize());
            config.setOverlap(info.get().getOverlap());
            config.setTopK(info.get().getTopK());
            config.setShortBookFullText(info.get().getShortBookFullText());
        } else {
            config.setChunkSize(500);
            config.setOverlap(100);
            config.setTopK(3);
            config.setShortBookFullText(true);
        }
        return config;
    }

    @Transactional
    public void updateConfig(String bookId, RagConfigDTO config) {
        RagIndexInfo info = ragIndexInfoRepository.findById(bookId)
                .orElseGet(() -> {
                    RagIndexInfo newInfo = new RagIndexInfo();
                    newInfo.setBookId(bookId);
                    return newInfo;
                });

        if (config.getChunkSize() != null) info.setChunkSize(config.getChunkSize());
        if (config.getOverlap() != null) info.setOverlap(config.getOverlap());
        if (config.getTopK() != null) info.setTopK(config.getTopK());
        if (config.getShortBookFullText() != null) info.setShortBookFullText(config.getShortBookFullText());
        info.setUpdatedAt(LocalDateTime.now());

        ragIndexInfoRepository.save(info);
    }
}
