package com.webbook.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SourceRecordDTO {
    private Integer chapterIndex;
    private String chapterTitle;
    private String excerpt;
    private Integer rank;
}
