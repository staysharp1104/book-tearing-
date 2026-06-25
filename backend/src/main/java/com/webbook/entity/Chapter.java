package com.webbook.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "chapters", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"book_id", "chapter_index"})
})
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "book_id", length = 64, nullable = false)
    private String bookId;

    @Column(name = "chapter_index", nullable = false)
    private Integer chapterIndex;

    @Column(name = "chapter_title", length = 512)
    private String chapterTitle;

    @Column(name = "chapter_url", length = 1024)
    private String chapterUrl;

    @Column(name = "content_path", length = 256)
    private String contentPath;

    @Column(name = "content_size")
    private Integer contentSize = 0;

    @Column(length = 32)
    private String source;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
