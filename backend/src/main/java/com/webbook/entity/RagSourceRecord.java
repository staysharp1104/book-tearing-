package com.webbook.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "rag_source_records", indexes = {
    @Index(name = "idx_chat_message", columnList = "chat_message_id"),
    @Index(name = "idx_book", columnList = "book_id")
})
public class RagSourceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_message_id", nullable = false)
    private Long chatMessageId;

    @Column(name = "book_id", length = 64, nullable = false)
    private String bookId;

    @Column(name = "chapter_index", nullable = false)
    private Integer chapterIndex;

    @Column(name = "chapter_title", length = 512)
    private String chapterTitle;

    @Column(columnDefinition = "TEXT")
    private String excerpt;

    @Column(nullable = false)
    private Integer rank = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
