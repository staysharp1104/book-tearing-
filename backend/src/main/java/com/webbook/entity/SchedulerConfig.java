package com.webbook.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "scheduler_config")
public class SchedulerConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "cron_expr", length = 64, nullable = false)
    private String cronExpr;

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(nullable = false)
    private Integer weekday = 0;

    @Column(nullable = false)
    private Integer hour = 2;

    @Column(nullable = false)
    private Integer minute = 0;

    @Column(name = "last_run_at")
    private LocalDateTime lastRunAt;

    @Column(name = "next_run_at")
    private LocalDateTime nextRunAt;

    @Column(name = "last_run_status", length = 32)
    private String lastRunStatus;

    @Column(name = "last_run_summary", length = 512)
    private String lastRunSummary;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
