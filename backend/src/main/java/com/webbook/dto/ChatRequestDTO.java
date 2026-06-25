package com.webbook.dto;

import lombok.Data;

@Data
public class ChatRequestDTO {
    private String bookId;
    private String message;
    private Integer promptTemplateId;  // optional
    private Boolean tenChapterContext; // whether to include 10 chapters as context
}
