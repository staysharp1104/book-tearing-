package com.webbook.rag;

import com.webbook.entity.Chapter;
import com.webbook.repository.ChapterRepository;
import com.webbook.util.GzipUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RAGIndexer {
    private static final Logger log = LoggerFactory.getLogger(RAGIndexer.class);

    private final ChapterRepository chapterRepository;
    private final GzipUtil gzipUtil;
    private final TextSplitter textSplitter;
    private final TfidfVectorizer tfidfVectorizer;
    private final RAGCacheManager cacheManager;

    @Value("${app.rag-cache-dir}")
    private String ragCacheDir;

    @Value("${app.data-dir}")
    private String dataDir;

    public RAGIndexer(ChapterRepository chapterRepository, GzipUtil gzipUtil,
                      TextSplitter textSplitter, TfidfVectorizer tfidfVectorizer,
                      RAGCacheManager cacheManager) {
        this.chapterRepository = chapterRepository;
        this.gzipUtil = gzipUtil;
        this.textSplitter = textSplitter;
        this.tfidfVectorizer = tfidfVectorizer;
        this.cacheManager = cacheManager;
    }

    public RAGIndex buildIndex(String bookId, int chunkSize, int overlap) {
        List<Chapter> chapters = chapterRepository.findByBookIdOrderByChapterIndexAsc(bookId);

        List<String> chunkTexts = new ArrayList<>();
        List<Integer> chapterIndices = new ArrayList<>();
        List<String> chapterTitles = new ArrayList<>();

        for (Chapter chapter : chapters) {
            String content = readChapterContent(chapter);
            if (content == null || content.isEmpty()) continue;

            List<TextSplitter.TextChunk> chunks = textSplitter.split(content, chunkSize, overlap);
            for (TextSplitter.TextChunk chunk : chunks) {
                chunkTexts.add(chunk.getContent());
                chapterIndices.add(chapter.getChapterIndex());
                chapterTitles.add(chapter.getChapterTitle());
            }
        }

        if (chunkTexts.isEmpty()) {
            throw new RuntimeException("No content available for indexing, bookId: " + bookId);
        }

        TfidfVectorizer.TfidfResult result = tfidfVectorizer.fit(chunkTexts);

        long totalWords = 0;
        for (String text : chunkTexts) {
            totalWords += text.length();
        }

        RAGIndex index = new RAGIndex();
        index.setBookId(bookId);
        index.setChunkTexts(chunkTexts);
        index.setVectors(result.getVectors());
        index.setIdf(result.getIdf());
        index.setChapterIndices(chapterIndices);
        index.setChapterTitles(chapterTitles);
        index.setChunkSize(chunkSize);
        index.setOverlap(overlap);
        index.setTotalWords(totalWords);
        index.setBuiltAt(System.currentTimeMillis());

        return index;
    }

    private String readChapterContent(Chapter chapter) {
        try {
            if (chapter.getContentPath() != null && !chapter.getContentPath().isEmpty()) {
                String fullPath = dataDir + "/" + chapter.getContentPath();
                return gzipUtil.readFile(fullPath);
            }
        } catch (Exception e) {
            log.warn("Failed to read chapter content: bookId={}, index={}", chapter.getBookId(), chapter.getChapterIndex(), e);
        }
        return "";
    }

    public String getCacheFilePath(String bookId) {
        return ragCacheDir + "/" + bookId + ".pkl";
    }
}
