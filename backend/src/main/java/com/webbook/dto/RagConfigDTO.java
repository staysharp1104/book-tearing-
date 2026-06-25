package com.webbook.dto;

import lombok.Data;

@Data
public class RagConfigDTO {
    private Integer chunkSize;
    private Integer overlap;
    private Integer topK;
    private Boolean shortBookFullText;
}
