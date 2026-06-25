package com.webbook.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "rag_index_info")
public class RagIndexInfo {
    @Id
    @Column(name = "book_id", length = 64)
    private String bookId;

    @Column(length = 32)
    private String status = "not_built";

    @Column(name = "chunk_size")
    private Integer chunkSize = 500;

    @Column(name = "overlap")
    private Integer overlap = 100;

    @Column(name = "top_k")
    private Integer topK = 3;

    @Column(name = "short_book_full_text")
    private Boolean shortBookFullText = true;

    @Column(name = "chunk_count")
    private Integer chunkCount = 0;

    @Column(name = "word_count")
    private Integer wordCount = 0;

    @Column(name = "built_at")
    private LocalDateTime builtAt;

    @Column(name = "cache_path", length = 256)
    private String cachePath;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
