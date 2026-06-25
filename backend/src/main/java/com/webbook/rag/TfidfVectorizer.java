package com.webbook.rag;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class TfidfVectorizer {

    public TfidfResult fit(List<String> documents) {
        int docCount = documents.size();
        Set<String> allTerms = new HashSet<>();
        List<Map<String, Integer>> termFreqs = new ArrayList<>();

        for (String doc : documents) {
            Map<String, Integer> tf = new HashMap<>();
            String[] terms = tokenize(doc);
            for (String term : terms) {
                tf.put(term, tf.getOrDefault(term, 0) + 1);
                allTerms.add(term);
            }
            termFreqs.add(tf);
        }

        Map<String, Double> idf = new HashMap<>();
        for (String term : allTerms) {
            int docWithTerm = 0;
            for (Map<String, Integer> tf : termFreqs) {
                if (tf.containsKey(term)) {
                    docWithTerm++;
                }
            }
            double idfValue = Math.log((double) (docCount + 1) / (docWithTerm + 1)) + 1.0;
            idf.put(term, idfValue);
        }

        List<Map<String, Double>> vectors = new ArrayList<>();
        for (Map<String, Integer> tf : termFreqs) {
            Map<String, Double> vector = new HashMap<>();
            for (Map.Entry<String, Integer> entry : tf.entrySet()) {
                double tfValue = 1 + Math.log(entry.getValue());
                double tfidf = tfValue * idf.get(entry.getKey());
                vector.put(entry.getKey(), tfidf);
            }
            vectors.add(vector);
        }

        return new TfidfResult(vectors, idf, allTerms);
    }

    public Map<String, Double> transform(String query, Map<String, Double> idf) {
        Map<String, Double> queryVector = new HashMap<>();
        String[] terms = tokenize(query);
        Map<String, Integer> tf = new HashMap<>();
        for (String term : terms) {
            tf.put(term, tf.getOrDefault(term, 0) + 1);
        }

        for (Map.Entry<String, Integer> entry : tf.entrySet()) {
            String term = entry.getKey();
            double tfValue = 1 + Math.log(entry.getValue());
            double idfValue = idf.getOrDefault(term, Math.log((double) (idf.size() + 1) / 1) + 1.0);
            queryVector.put(term, tfValue * idfValue);
        }

        return queryVector;
    }

    private String[] tokenize(String text) {
        List<String> tokens = new ArrayList<>();

        String chineseOnly = text.replaceAll("[^\\u4e00-\\u9fff]", " ").trim();
        if (!chineseOnly.isEmpty()) {
            String[] words = chineseOnly.split("\\s+");
            for (String word : words) {
                if (word.isEmpty()) continue;
                for (int i = 0; i < word.length() - 1; i++) {
                    tokens.add(word.substring(i, i + 2));
                }
                for (int i = 0; i < word.length(); i++) {
                    tokens.add(String.valueOf(word.charAt(i)));
                }
            }
        }

        return tokens.toArray(new String[0]);
    }

    public static class TfidfResult {
        private final List<Map<String, Double>> vectors;
        private final Map<String, Double> idf;
        private final Set<String> vocabulary;

        public TfidfResult(List<Map<String, Double>> vectors, Map<String, Double> idf, Set<String> vocabulary) {
            this.vectors = vectors;
            this.idf = idf;
            this.vocabulary = vocabulary;
        }

        public List<Map<String, Double>> getVectors() { return vectors; }
        public Map<String, Double> getIdf() { return idf; }
        public Set<String> getVocabulary() { return vocabulary; }
    }
}
