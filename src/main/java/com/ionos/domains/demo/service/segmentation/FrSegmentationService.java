package com.ionos.domains.demo.service.segmentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import static com.ionos.domains.demo.configuration.ApplicationConfiguration.*;

@Service
public class FrSegmentationService extends AbstractSegmentationService {

    @Autowired
    private ProbabilityService frUnigramsDistribution;

    @Autowired
    private ProbabilityService frBigramsDistribution;

    @Autowired
    private JedisPool jedisPool;

    @Override
    double conditionalProbability(String word, String prev) {
        Jedis jedis = jedisPool.getResource();
        if (jedis.hexists(FR_BIGRAM, prev + " " + word) && jedis.hexists(FR_UNIGRAM, prev)) {
            final var prob = (double) Long.parseLong(jedis.hget(FR_BIGRAM, prev + " " + word)) / Long.parseLong(jedis.hget(FR_UNIGRAM, prev));
            jedis.close();
            return prob;
        } else {
            jedis.close();
            return frUnigramsDistribution.getGramProbability(word);
        }

        /*if (frBigramsDistribution.getData().containsKey(prev + " " + word) && frUnigramsDistribution.getData().get(prev) != null) {
            return (double)frBigramsDistribution.getData().get(prev + " " + word) / frUnigramsDistribution.getData().get(prev);
        } else {
            return frUnigramsDistribution.getGramProbabilityMap(word);
        }*/
    }
}
