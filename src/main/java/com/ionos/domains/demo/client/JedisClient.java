package com.ionos.domains.demo.client;

import com.ionos.domains.demo.model.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;

@Component
public class JedisClient {

    public static final String DOMAIN_EN = "Domain-EN";
    public static final String DOMAIN_ES = "Domain-ES";
    public static final String DOMAIN_DE = "Domain-DE";
    public static final String DOMAIN_FR = "Domain-FR";
    public static final String DOMAIN_IT = "Domain-IT";

    public static final String EN_WORD = "EN-WordEmbd";
    public static final String DE_WORD = "DE-WordEmbd";
    public static final String ES_WORD = "ES-WordEmbd";
    public static final String FR_WORD = "FR-WordEmbd";
    public static final String IT_WORD = "IT-WordEmbd";

    @Autowired
    private JedisPool jedisPool;

    public Map<String, String> getSegmentedDomainsFromRedis(Language language) {
        Map<String, String> segmentedDomains;
        Jedis jedis = jedisPool.getResource();
        switch (language) {
            case EN:
                segmentedDomains = jedis.hgetAll(DOMAIN_EN);
                break;
            case DE:
                segmentedDomains = jedis.hgetAll(DOMAIN_DE);
                break;
            case ES:
                segmentedDomains = jedis.hgetAll(DOMAIN_ES);
                break;
            case FR:
                segmentedDomains = jedis.hgetAll(DOMAIN_FR);
                break;
            case IT:
                segmentedDomains = jedis.hgetAll(DOMAIN_IT);
                break;
            default:
                jedis.close();
                throw new RuntimeException("wrong language");
        }
        jedis.close();
       return segmentedDomains;
    }

    public List<double[]> getWordEmbeddings(List<String> words, Language language) {
        List<double[]> embeddings = new ArrayList<>();
        Jedis jedis = jedisPool.getResource();
        switch (language) {
            case EN:
                for (String word : words) {
                    String embedding = jedis.hget(EN_WORD, word);
                    if (embedding == null) {
                        embeddings.add(null);
                        continue;
                    }
                    embeddings.add(Arrays.stream(embedding.split(" ")).mapToDouble(Double::parseDouble).toArray());
                }
                break;
            case DE:
                for (String word : words) {
                    String embedding = jedis.hget(DE_WORD, word);
                    if (embedding == null) {
                        embeddings.add(null);
                        continue;
                    }
                    embeddings.add(Arrays.stream(embedding.split(" ")).mapToDouble(Double::parseDouble).toArray());
                }
                break;
            case ES:
                for (String word : words) {
                    String embedding = jedis.hget(ES_WORD, word);
                    if (embedding == null) {
                        embeddings.add(null);
                        continue;
                    }
                    embeddings.add(Arrays.stream(embedding.split(" ")).mapToDouble(Double::parseDouble).toArray());
                }
                break;
            case FR:
                for (String word : words) {
                    String embedding = jedis.hget(FR_WORD, word);
                    if (embedding == null) {
                        embeddings.add(null);
                        continue;
                    }
                    embeddings.add(Arrays.stream(embedding.split(" ")).mapToDouble(Double::parseDouble).toArray());
                }
                break;
            case IT:
                for (String word : words) {
                    String embedding = jedis.hget(IT_WORD, word);
                    if (embedding == null) {
                        embeddings.add(null);
                        continue;
                    }
                    embeddings.add(Arrays.stream(embedding.split(" ")).mapToDouble(Double::parseDouble).toArray());
                }
                break;
            default:
                jedis.close();
                throw new RuntimeException();
        }
        jedis.close();
        return embeddings;
    }

    public Set<String> getDomainsFromRedis(Language language) {
        Map<String, String> segmentedDomains;
        Jedis jedis = jedisPool.getResource();
        switch (language) {
            case EN:
                segmentedDomains = jedis.hgetAll(DOMAIN_EN);
                break;
            case DE:
                segmentedDomains = jedis.hgetAll(DOMAIN_DE);
                break;
            case ES:
                segmentedDomains = jedis.hgetAll(DOMAIN_ES);
                break;
            case FR:
                segmentedDomains = jedis.hgetAll(DOMAIN_FR);
                break;
            case IT:
                segmentedDomains = jedis.hgetAll(DOMAIN_IT);
                break;
            default:
                jedis.close();
                throw new RuntimeException("wrong language");
        }
        jedis.close();
        return segmentedDomains.keySet();
    }
}
