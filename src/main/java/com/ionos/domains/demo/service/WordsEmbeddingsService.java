package com.ionos.domains.demo.service;

import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

//@Service
public class WordsEmbeddingsService {
    //private Jedis jedis;
    private JedisPool jedisPool;
    Map<String, double[]> wordEmbeddings = new HashMap<>();
    int N = 300;
    private String key;

    public WordsEmbeddingsService(String fileName, String key, JedisPool jedisPool) throws Exception {
        System.out.println("Loading " + key);
        this.jedisPool = jedisPool;
        Jedis jedis = jedisPool.getResource();
        this.key = key;
        if (Boolean.FALSE.equals(jedis.exists(key))) {
            try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
                long nr = 1;
                String line = br.readLine();
                while (line != null) {
                    String word = line.substring(0, line.indexOf(" "));
                    String embedding = line.substring(line.indexOf(" ") + 1);
                    //jedis = jedisPool.getResource();
                    jedis.hsetnx(key, word.toLowerCase(), embedding);
                    //jedis.close();
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
        jedis.close();
    }

    public double getCosineSimilarity(double[] v1, double[] v2) {
        if (v1 == null || v2 == null) {
            return 0.0;
        }

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i<v1.length; i++) {
            dotProduct += v1[i]*v2[i];
            norm1 += v1[i]*v1[i];
            norm2 += v2[i]*v2[i];
        }

        if (norm1 == 0 || norm2 == 0) {
            return 0.0;
        }

        norm1 = Math.sqrt(norm1);
        norm2 = Math.sqrt(norm2);

        return dotProduct / (norm1*norm2);
    }

    public double getCosSimilarity(String word1, String word2) {
        String embeddingText1;
        String embeddingText2;
        Jedis jedis;
        jedis = jedisPool.getResource();
        embeddingText1 = jedis.hget(key, word1);
        embeddingText2 = jedis.hget(key, word2);
        jedis.close();

        if (embeddingText1 == null || embeddingText2 == null) {
            return 0.0;
        }

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        String [] embedding1 = embeddingText1.split(" ");
        String [] embedding2 = embeddingText2.split(" ");
        for (int i = 0; i<embedding1.length; i++) {
            double s1 = Double.parseDouble(embedding1[i]);
            double s2 = Double.parseDouble(embedding2[i]);
            dotProduct += s1*s2;
            norm1 += s1*s1;
            norm2 += s2*s2;
        }

        norm1 = Math.sqrt(norm1);
        norm2 = Math.sqrt(norm2);
        /*double[] word1Embedding = Arrays.stream(embeddingText1.split(" ")).mapToDouble(Double::parseDouble).toArray();
        double[] word2Embedding = Arrays.stream(embeddingText2.split(" ")).mapToDouble(Double::parseDouble).toArray();

        assert word1Embedding.length == word2Embedding.length;
        double dotProduct = dotProduct(word1Embedding, word2Embedding);
        double sumNorm = vectorNorm(word1Embedding) * vectorNorm(word2Embedding);*/
        return dotProduct / (norm1*norm2);
    }

    public double getEuclidianDistance(String word1, String word2) {
        Jedis jedis = jedisPool.getResource();
        if (Boolean.FALSE.equals(jedis.hexists(key, word1)) || Boolean.FALSE.equals(jedis.hexists(key, word2))) {
            return 0.0;
        }
        double distance = 0.0;
        String[] embeddingText1 = jedis.hget(key, word1).split(" ");
        String[] embeddingText2 = jedis.hget(key, word2).split(" ");
        for (int i=0; i<N; i++) {
            distance += Math.pow(Double.parseDouble(embeddingText1[i]) - Double.parseDouble(embeddingText2[i]), 2);
        }
        return Math.sqrt(distance);
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
