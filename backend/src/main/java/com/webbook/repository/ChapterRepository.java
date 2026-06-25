package com.webbook.repository;

import com.webbook.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    List<Chapter> findByBookIdOrderByChapterIndexAsc(String bookId);

    List<Chapter> findByBookIdAndChapterIndexBetweenOrderByChapterIndexAsc(String bookId, int start, int end);

    int countByBookId(String bookId);
}
