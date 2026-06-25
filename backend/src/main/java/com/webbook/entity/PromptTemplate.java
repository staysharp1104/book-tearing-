package com.webbook.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "prompt_templates")
public class PromptTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 128, nullable = false)
    private String name;

    @Column(length = 512)
    private String description;

    @Column(length = 64)
    private String scene;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "is_quick_btn")
    private Boolean isQuickBtn = false;

    @Column(name = "is_system")
    private Boolean isSystem = false;

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
