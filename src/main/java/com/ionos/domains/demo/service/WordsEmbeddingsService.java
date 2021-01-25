package com.ionos.domains.demo.service;

import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

//@Service
public class WordsEmbeddingsService {
    private Jedis jedis;
    Map<String, double[]> wordEmbeddings = new HashMap<>();
    int N = 300;
    private String key;

    public WordsEmbeddingsService(String fileName, String key, Jedis jedis) throws Exception {
        this.jedis = jedis;
        this.key = key;
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            long nr = 1;
            String line = br.readLine();
            while (line != null) {
                String word = line.substring(0, line.indexOf(" "));
                String embedding = line.substring(line.indexOf(" ")+1);
                jedis.hsetnx(key, word, embedding);
                /*String [] parts = line.split(" ");
                double[] embeddingsVector = new double[N];
                for (int i=1; i< parts.length; i++) {
                    embeddingsVector[i-1] = Double.parseDouble(parts[i]);
                }
                wordEmbeddings.put(parts[0], embeddingsVector);*/
                System.out.println(nr++);
                line = br.readLine();
            }
        }
    }

    public double getCosSimilarity(String word1, String word2) {
        if (Boolean.FALSE.equals(jedis.hexists(key, word1)) || Boolean.FALSE.equals(jedis.hexists(key, word2))) {
            return 0.0;
        }
        String embeddingText1 = jedis.hget(key, word1);
        String embeddingText2 = jedis.hget(key, word2);
        double[] word1Embedding = Arrays.stream(embeddingText1.split(" ")).mapToDouble(Double::parseDouble).toArray();
        double[] word2Embedding = Arrays.stream(embeddingText2.split(" ")).mapToDouble(Double::parseDouble).toArray();
        /*if (word1Embedding == null || word2Embedding == null) {
            return 0.0;
        }*/

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
}
