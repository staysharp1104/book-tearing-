package com.webbook.rag;

import lombok.Data;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class RAGIndex implements Serializable {
    private static final long serialVersionUID = 1L;

    private String bookId;
    private List<String> chunkTexts;
    private List<Map<String, Double>> vectors;
    private Map<String, Double> idf;
    private List<Integer> chapterIndices;
    private List<String> chapterTitles;
    private int chunkSize;
    private int overlap;
    private long totalWords;
    private long builtAt;
}
