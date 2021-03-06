package com.ionos.domains.demo.service.segmentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import static com.ionos.domains.demo.configuration.ApplicationConfiguration.*;

@Service
public class EsSegmentationService extends AbstractSegmentationService {

    @Autowired
    private ProbabilityService esUnigramsDistribution;

    @Autowired
    private ProbabilityService esBigramsDistribution;

    @Autowired
    private JedisPool jedisPool;

    @Override
    double conditionalProbability(String word, String prev) {
        Jedis jedis = jedisPool.getResource();
        if (jedis.hexists(ES_BIGRAM, prev + " " + word) && jedis.hexists(ES_UNIGRAM, prev)) {
            final var prob = (double) Long.parseLong(jedis.hget(ES_BIGRAM, prev + " " + word)) / Long.parseLong(jedis.hget(ES_UNIGRAM, prev));
            jedis.close();
            return prob;
        } else {
            jedis.close();
            return esUnigramsDistribution.getGramProbability(word);
        }

        /*if (esBigramsDistribution.getData().containsKey(prev + " " + word) && esUnigramsDistribution.getData().get(prev) != null) {
            return (double)esBigramsDistribution.getData().get(prev + " " + word) / esUnigramsDistribution.getData().get(prev);
        } else {
            return esUnigramsDistribution.getGramProbabilityMap(word);
        }*/
    }
}
