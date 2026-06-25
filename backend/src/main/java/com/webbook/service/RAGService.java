package com.webbook.service;

import com.webbook.dto.AnalysisPageDTO;
import com.webbook.dto.RagStatusDTO;
import com.webbook.dto.SourceRecordDTO;
import com.webbook.entity.Book;
import com.webbook.entity.Chapter;
import com.webbook.entity.PromptTemplate;
import com.webbook.entity.RagIndexInfo;
import com.webbook.rag.*;
import com.webbook.repository.RagIndexInfoRepository;
import com.webbook.repository.RagSourceRecordRepository;
import com.webbook.repository.PromptTemplateRepository;
import com.webbook.repository.ChapterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RAGService {
    private static final Logger log = LoggerFactory.getLogger(RAGService.class);

    private final RagIndexInfoRepository ragIndexInfoRepository;
    private final RagSourceRecordRepository ragSourceRecordRepository;
    private final PromptTemplateRepository promptTemplateRepository;
    private final ChapterRepository chapterRepository;
    private final ChapterService chapterService;
    private final RAGIndexer indexer;
    private final RAGCacheManager cacheManager;
    private final TextSplitter textSplitter;
    private final TfidfVectorizer tfidfVectorizer;
    private final RAGSearcher searcher;

    public RAGService(RagIndexInfoRepository ragIndexInfoRepository,
                      RagSourceRecordRepository ragSourceRecordRepository,
                      PromptTemplateRepository promptTemplateRepository,
                      ChapterRepository chapterRepository,
                      ChapterService chapterService,
                      RAGIndexer indexer,
                      RAGCacheManager cacheManager,
                      TextSplitter textSplitter,
                      TfidfVectorizer tfidfVectorizer,
                      RAGSearcher searcher) {
        this.ragIndexInfoRepository = ragIndexInfoRepository;
        this.ragSourceRecordRepository = ragSourceRecordRepository;
        this.promptTemplateRepository = promptTemplateRepository;
        this.chapterRepository = chapterRepository;
        this.chapterService = chapterService;
        this.indexer = indexer;
        this.cacheManager = cacheManager;
        this.textSplitter = textSplitter;
        this.tfidfVectorizer = tfidfVectorizer;
        this.searcher = searcher;
    }

    public List<SourceRecordDTO> searchWithSources(String bookId, String query) {
        Optional<RagIndexInfo> infoOpt = ragIndexInfoRepository.findById(bookId);
        if (infoOpt.isEmpty() || !"built".equals(infoOpt.get().getStatus())) {
            return List.of();
        }

        RagIndexInfo info = infoOpt.get();
        String cachePath = indexer.getCacheFilePath(bookId);

        try {
            RAGIndex index = cacheManager.loadIndex(cachePath);
            if (index == null) {
                return List.of();
            }

            int topK = info.getTopK() != null ? info.getTopK() : 3;

            Map<String, Double> queryVector = tfidfVectorizer.transform(query, index.getIdf());

            List<RAGSearcher.ScoredChunk> results = searcher.search(
                    queryVector,
                    index.getVectors(),
                    index.getChunkTexts(),
                    index.getChapterIndices(),
                    index.getChapterTitles(),
                    topK
            );

            List<SourceRecordDTO> sources = new ArrayList<>();
            for (int i = 0; i < results.size(); i++) {
                RAGSearcher.ScoredChunk chunk = results.get(i);
                sources.add(new SourceRecordDTO(
                        chunk.getChapterIndex(),
                        chunk.getChapterTitle(),
                        truncate(chunk.getContent(), 300),
                        i + 1
                ));
            }
            return sources;

        } catch (Exception e) {
            log.error("RAG search failed for bookId: {}", bookId, e);
            return List.of();
        }
    }

    public List<AnalysisPageDTO.ChapterDTO> getTenChapterContext(String bookId) {
        List<Chapter> chapters = chapterService.findChaptersByRange(bookId, 1, 10);
        List<AnalysisPageDTO.ChapterDTO> result = new ArrayList<>();
        for (Chapter ch : chapters) {
            AnalysisPageDTO.ChapterDTO dto = new AnalysisPageDTO.ChapterDTO();
            dto.setIndex(ch.getChapterIndex());
            dto.setTitle(ch.getChapterTitle());
            dto.setContent(chapterService.readChapterContent(ch));
            result.add(dto);
        }
        return result;
    }

    private String truncate(String text, int maxLen) {
        if (text == null) return "";
        return text.length() <= maxLen ? text : text.substring(0, maxLen) + "...";
    }

    public List<RagStatusDTO> getAllBuiltStatus() {
        List<RagIndexInfo> all = ragIndexInfoRepository.findAll();
        return all.stream().map(i -> new RagStatusDTO(
                i.getBookId(), i.getStatus(), i.getChunkSize(), i.getOverlap(),
                i.getTopK(), i.getShortBookFullText(),
                i.getChunkCount(), i.getWordCount(), i.getBuiltAt()
        )).collect(Collectors.toList());
    }
}
