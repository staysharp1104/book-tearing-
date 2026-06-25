package com.webbook.rag;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TextSplitter {

    public List<TextChunk> split(String text, int chunkSize, int overlap) {
        List<TextChunk> chunks = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return chunks;
        }

        text = text.replaceAll("\\s+", " ").trim();

        int start = 0;
        int chunkIndex = 0;
        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());
            String content = text.substring(start, end);
            chunks.add(new TextChunk(chunkIndex++, content, start, end));

            if (end >= text.length()) {
                break;
            }

            start = end - overlap;
            if (start < 0) start = 0;
        }

        return chunks;
    }

    public static class TextChunk {
        private final int index;
        private final String content;
        private final int startPos;
        private final int endPos;

        public TextChunk(int index, String content, int startPos, int endPos) {
            this.index = index;
            this.content = content;
            this.startPos = startPos;
            this.endPos = endPos;
        }

        public int getIndex() { return index; }
        public String getContent() { return content; }
        public int getStartPos() { return startPos; }
        public int getEndPos() { return endPos; }
    }
}
