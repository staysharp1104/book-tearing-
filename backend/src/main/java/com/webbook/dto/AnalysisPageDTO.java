package com.webbook.dto;

import lombok.Data;
import java.util.List;

@Data
public class AnalysisPageDTO {
    private BookDTO book;
    private List<ChapterDTO> chapters;
    private RagStatusDTO ragStatus;
    private List<PromptTemplateDTO> quickPrompts;

    @Data
    public static class BookDTO {
        private String bookId;
        private String title;
        private String author;
        private String source;
        private String intro;
        private String coverUrl;
    }

    @Data
    public static class ChapterDTO {
        private Integer index;
        private String title;
        private String content;
    }
}
