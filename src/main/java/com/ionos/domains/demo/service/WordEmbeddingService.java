package com.ionos.domains.demo.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class WordEmbeddingService {
    public static final String FILE_NAME = "/home/enghin/Documents/Personal/Projects/wordSuggestion/datasets/cc.en.300.vec";
    Map<String, double[]> wordEmbeddings = new HashMap<>();
    int N = 300;

    public WordEmbeddingService() throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            long nr = 1;
            String line = br.readLine();
            line = br.readLine();
            while (line != null) {
                String [] parts = line.split(" ");
                double[] embeddingsVector = new double[N];
                for (int i=1; i< parts.length; i++) {
                    embeddingsVector[i-1] = Double.parseDouble(parts[i]);
                }
                wordEmbeddings.put(parts[0], embeddingsVector);
                System.out.println(nr++);
                line = br.readLine();
            }
        }
    }

    public double getCosSimilarity(String word1, String word2) {
        double[] word1Embedding = wordEmbeddings.get(word1);
        double[] word2Embedding = wordEmbeddings.get(word2);
        if (word1Embedding == null || word2Embedding == null) {
            return 0.0;
        }

        assert word1Embedding.length == word2Embedding.length;
        double dotProduct = dotProduct(word1Embedding, word2Embedding);
        double sumNorm = vectorNorm(word1Embedding) * vectorNorm(word2Embedding);
        return dotProduct / sumNorm;
    }

    private double dotProduct(double[] v1, double[] v2) {
        assert v1.length == v2.length;
        double result = 0;
        for (int i = 0; i < v1.length; i++) {
            result += v1[i] * v2[i];
        }
        return result;
    }
    private double vectorNorm(double[] v) {
        double result = 0;
        for (double aV : v) {
            result += aV * aV;
        }
        result = Math.sqrt(result);
        return result;
    }

    public static void main(String[] args) throws Exception {
        WordEmbeddingService wordEmbeddingService = new WordEmbeddingService();
        System.out.println(wordEmbeddingService.getCosSimilarity("sport", "running"));
        System.out.println(wordEmbeddingService.getCosSimilarity("shoes", "running"));
        System.out.println(wordEmbeddingService.getCosSimilarity("cat", "dog"));
    }
}
