package com.webbook.service;

import com.webbook.entity.Chapter;
import com.webbook.repository.ChapterRepository;
import com.webbook.util.GzipUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final GzipUtil gzipUtil;
    private final String dataDir;

    public ChapterService(ChapterRepository chapterRepository, GzipUtil gzipUtil,
                          @Value("${app.data-dir}") String dataDir) {
        this.chapterRepository = chapterRepository;
        this.gzipUtil = gzipUtil;
        this.dataDir = dataDir;
    }

    public List<Chapter> findByBookId(String bookId) {
        return chapterRepository.findByBookIdOrderByChapterIndexAsc(bookId);
    }

    public List<Chapter> findChaptersByRange(String bookId, int start, int end) {
        return chapterRepository.findByBookIdAndChapterIndexBetweenOrderByChapterIndexAsc(bookId, start, end);
    }

    public String readChapterContent(Chapter chapter) {
        try {
            if (chapter.getContentPath() != null && !chapter.getContentPath().isEmpty()) {
                String fullPath = dataDir + "/" + chapter.getContentPath();
                return gzipUtil.readFile(fullPath);
            }
        } catch (Exception e) {
            // ignore
        }
        return "";
    }

    public int countByBookId(String bookId) {
        return chapterRepository.countByBookId(bookId);
    }

    public Optional<Chapter> findById(Long id) {
        return chapterRepository.findById(id);
    }
}
