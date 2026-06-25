package com.webbook.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "chat_history")
public class ChatHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "book_id", length = 64)
    private String bookId;

    @Column(length = 16)
    private String role;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(length = 32)
    private String source;

    @Column(name = "create_time")
    private LocalDateTime createTime;
}
