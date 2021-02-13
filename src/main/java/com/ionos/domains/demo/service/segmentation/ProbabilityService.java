package com.ionos.domains.demo.service.segmentation;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ProbabilityService {

    private long totalTokens = 1024908267229L; // nr of tokens in corpus
    private long maxTotalTokens = 4178523593L;
    private final Map<String, Long> data = new HashMap<>();
    protected BufferedReader reader;
    protected String key;
    private final JedisPool jedisPool;
    private Jedis jedis;

    public ProbabilityService(String fileName, String key, JedisPool jedisPool, long nrTokens) throws IOException {
        System.out.println("Loading " + key);
        File file = new File(fileName);
        this.key = key;
        this.jedisPool = jedisPool;
        this.totalTokens = nrTokens;
        jedis = jedisPool.getResource();
        if (Boolean.FALSE.equals(jedis.exists(key))) {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"ISO-8859-1"));
            String line = reader.readLine();
            while (line != null) {
                final String[] split = line.split("\\t");
                jedis.hsetnx(key, split[0].toLowerCase(), split[1]);
                line = reader.readLine();
            }
        }
        jedis.close();
    }

    public double getGramProbability(String gram) {
        jedis = jedisPool.getResource();
        if (Boolean.TRUE.equals(jedis.hexists(key, gram))) {
            jedis.close();
            return (double) Long.parseLong(jedis.hget(key, gram))/ totalTokens;
        }
        jedis.close();
        return getProbabilityUnkGram(gram);
    }

    private double getProbabilityUnkGram(String gram) {
        // avoid long words
        return 10/(maxTotalTokens * (Math.pow(10, gram.length())));
    }

}
