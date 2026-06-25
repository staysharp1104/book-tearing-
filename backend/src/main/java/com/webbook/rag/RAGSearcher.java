package com.webbook.rag;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RAGSearcher {

    public List<ScoredChunk> search(Map<String, Double> queryVector,
                                     List<Map<String, Double>> docVectors,
                                     List<String> docTexts,
                                     List<Integer> docChapterIndices,
                                     List<String> docChapterTitles,
                                     int topK) {
        List<ScoredChunk> scored = new ArrayList<>();

        for (int i = 0; i < docVectors.size(); i++) {
            double similarity = cosineSimilarity(queryVector, docVectors.get(i));
            scored.add(new ScoredChunk(
                    i,
                    docTexts.get(i),
                    similarity,
                    docChapterIndices.get(i),
                    docChapterTitles.get(i)
            ));
        }

        scored.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));

        return scored.subList(0, Math.min(topK, scored.size()));
    }

    private double cosineSimilarity(Map<String, Double> vec1, Map<String, Double> vec2) {
        double dotProduct = 0;
        double norm1 = 0;
        double norm2 = 0;

        for (Map.Entry<String, Double> entry : vec1.entrySet()) {
            String term = entry.getKey();
            double val1 = entry.getValue();
            norm1 += val1 * val1;

            Double val2 = vec2.get(term);
            if (val2 != null) {
                dotProduct += val1 * val2;
            }
        }

        for (Map.Entry<String, Double> entry : vec2.entrySet()) {
            norm2 += entry.getValue() * entry.getValue();
        }

        if (norm1 == 0 || norm2 == 0) return 0;
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    public static class ScoredChunk {
        private final int index;
        private final String content;
        private final double score;
        private final int chapterIndex;
        private final String chapterTitle;

        public ScoredChunk(int index, String content, double score, int chapterIndex, String chapterTitle) {
            this.index = index;
            this.content = content;
            this.score = score;
            this.chapterIndex = chapterIndex;
            this.chapterTitle = chapterTitle;
        }

        public int getIndex() { return index; }
        public String getContent() { return content; }
        public double getScore() { return score; }
        public int getChapterIndex() { return chapterIndex; }
        public String getChapterTitle() { return chapterTitle; }
    }
}
