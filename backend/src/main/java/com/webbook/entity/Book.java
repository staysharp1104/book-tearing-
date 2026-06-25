package com.webbook.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "books")
public class Book {
    @Id
    @Column(name = "book_id", length = 64)
    private String bookId;

    @Column(nullable = false, length = 512)
    private String title;

    @Column(length = 256)
    private String author;

    @Column(name = "book_url", length = 1024)
    private String bookUrl;

    @Column(columnDefinition = "TEXT")
    private String intro;

    @Column(length = 128)
    private String category;

    @Column(name = "word_count")
    private Integer wordCount = 0;

    @Column(length = 32)
    private String status;

    @Column(length = 32)
    private String source;

    @Column(name = "chapter_count")
    private Integer chapterCount = 0;

    @Column(name = "total_chapters")
    private Integer totalChapters = 0;

    @Column(name = "crawl_status")
    private Integer crawlStatus = 0;

    @Column(name = "cover_url", length = 512)
    private String coverUrl;

    @Column(name = "cover_path", length = 256)
    private String coverPath;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
