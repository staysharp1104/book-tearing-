package com.webbook.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RagStatusDTO {
    private String bookId;
    private String status;           // not_built / built / rebuilding
    private Integer chunkSize;
    private Integer overlap;
    private Integer topK;
    private Boolean shortBookFullText;
    private Integer chunkCount;
    private Integer wordCount;
    private LocalDateTime builtAt;
}
