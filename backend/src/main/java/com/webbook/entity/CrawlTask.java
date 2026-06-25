package com.webbook.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "crawl_tasks")
public class CrawlTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_type", length = 32, nullable = false)
    private String taskType;

    @Column(length = 32)
    private String source;

    @Column(name = "target_id", length = 64)
    private String targetId;

    @Column(nullable = false)
    private Integer status = 0;

    @Column(name = "retry_count")
    private Integer retryCount = 0;

    @Column(name = "error_msg", columnDefinition = "TEXT")
    private String errorMsg;

    private Integer priority = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
