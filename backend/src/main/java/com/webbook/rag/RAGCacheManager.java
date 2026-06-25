package com.webbook.rag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class RAGCacheManager {
    private static final Logger log = LoggerFactory.getLogger(RAGCacheManager.class);

    public void saveIndex(RAGIndex index, String filePath) throws IOException {
        Path parentDir = Path.of(filePath).getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(index);
        }
        log.info("RAG index saved to: {}", filePath);
    }

    public RAGIndex loadIndex(String filePath) throws IOException, ClassNotFoundException {
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (RAGIndex) ois.readObject();
        }
    }

    public boolean deleteIndex(String filePath) {
        try {
            Path path = Path.of(filePath);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error("Failed to delete RAG cache: {}", filePath, e);
            return false;
        }
    }

    public boolean indexExists(String filePath) {
        return Files.exists(Path.of(filePath));
    }
}
