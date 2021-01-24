package com.ionos.domains.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProbabilityService {

    private final long N = 1024908267229L; // nr of tokens in corpus
    private Map<String, Long> data = new HashMap<>();
    protected BufferedReader reader;
    protected String key;
    private Jedis jedis;

    public ProbabilityService(String fileName, String key, Jedis jedis) throws IOException {
        this.key = key;
        this.jedis = jedis;
        reader = new BufferedReader(new FileReader(fileName));
        String line = reader.readLine();
        while (line != null) {
            final String[] split = line.split("\\t");
            //long frequency = Long.parseLong(split[1]);
            //data.put(split[0], frequency);
            //this.numberOfTokens += frequency;
            jedis.hsetnx(key, split[0], split[1]);
            line = reader.readLine();
        }
    }

    public double getGramProbability(String gram) {
        if (jedis.hexists(key, gram)) {
            return (double) Long.parseLong(jedis.hget(key, gram))/N;
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
        return 10/(N * (Math.pow(10, gram.length())));
    }
}
