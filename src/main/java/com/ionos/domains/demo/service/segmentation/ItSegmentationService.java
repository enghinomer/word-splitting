package com.ionos.domains.demo.service.segmentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import static com.ionos.domains.demo.configuration.ApplicationConfiguration.*;

@Service
public class ItSegmentationService extends AbstractSegmentationService {

    @Autowired
    private ProbabilityService itUnigramsDistribution;

    @Autowired
    private ProbabilityService itBigramsDistribution;

    @Autowired
    private JedisPool jedisPool;

    @Override
    double conditionalProbability(String word, String prev) {
        Jedis jedis = jedisPool.getResource();
        if (jedis.hexists(IT_BIGRAM, prev + " " + word) && jedis.hexists(IT_UNIGRAM, prev)) {
            final var prob = (double) Long.parseLong(jedis.hget(IT_BIGRAM, prev + " " + word)) / Long.parseLong(jedis.hget(IT_UNIGRAM, prev));
            jedis.close();
            return prob;
        } else {
            jedis.close();
            return itUnigramsDistribution.getGramProbability(word);
        }

        /*if (itBigramsDistribution.getData().containsKey(prev + " " + word) && itUnigramsDistribution.getData().get(prev) != null) {
            return (double)itBigramsDistribution.getData().get(prev + " " + word) / itUnigramsDistribution.getData().get(prev);
        } else {
            return itUnigramsDistribution.getGramProbabilityMap(word);
        }*/
    }
}
