package com.webbook.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "rank_books", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"source", "book_id"})
})
public class RankBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "book_id", length = 64, nullable = false)
    private String bookId;

    @Column(length = 16)
    private String rank;

    @Column(length = 512)
    private String title;

    @Column(length = 256)
    private String author;

    @Column(name = "book_url", length = 1024)
    private String bookUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 32)
    private String status;

    @Column(name = "reader_count", length = 32)
    private String readerCount;

    @Column(name = "category_label", length = 128)
    private String categoryLabel;

    @Column(length = 32)
    private String source;

    @Column(name = "cover_url", length = 512)
    private String coverUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
