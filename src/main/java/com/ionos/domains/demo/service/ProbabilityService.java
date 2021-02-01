package com.ionos.domains.demo.service;

import redis.clients.jedis.Jedis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProbabilityService {

    private long totalTokens = 1024908267229L; // nr of tokens in corpus
    private final Map<String, Long> data = new HashMap<>();
    protected BufferedReader reader;
    protected String key;
    private final Jedis jedis;

    public ProbabilityService(String fileName, String key, Jedis jedis, long nrTokens) throws IOException {
        System.out.println("Loading " + key);
        this.key = key;
        this.jedis = jedis;
        this.totalTokens = nrTokens;
        reader = new BufferedReader(new FileReader(fileName));
        String line = reader.readLine();
        while (line != null) {
            final String[] split = line.split("\\t");
            long frequency = Long.parseLong(split[1]);
            data.put(split[0], frequency);
            //this.numberOfTokens += frequency;
            jedis.hsetnx(key, split[0], split[1]);
            line = reader.readLine();
        }
    }

    public double getGramProbability(String gram) {
        if (Boolean.TRUE.equals(jedis.hexists(key, gram))) {
            return (double) Long.parseLong(jedis.hget(key, gram))/ totalTokens;
        }
        /*if (data.containsKey(gram)) {
            return (double)data.get(gram)/N;
        }*/
        return getProbabilityUnkGram(gram);
    }

    public Map<String, Long> getData() {
        return data;
    }

    private double getProbabilityUnkGram(String gram) {
        // avoid long words
        return 10/(totalTokens * (Math.pow(10, gram.length())));
    }

    public double getGramProbabilityMap(String gram) {
        if (data.containsKey(gram)) {
            return (double)data.get(gram)/ totalTokens;
        }
        return getProbabilityUnkGram(gram);
    }

}
