package com.webbook.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PromptTemplateDTO {
    private Integer id;
    private String name;
    private String description;
    private String scene;
    private String content;
    private Boolean isQuickBtn;
    private Boolean isSystem;
    private Boolean enabled;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
