package com.ionos.domains.demo.service.segmentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import static com.ionos.domains.demo.configuration.ApplicationConfiguration.EN_BIGRAM;
import static com.ionos.domains.demo.configuration.ApplicationConfiguration.EN_UNIGRAM;

@Service
public class EnSegmentationService extends AbstractSegmentationService {

    @Autowired
    private ProbabilityService enUnigramsDistribution;

    @Autowired
    private ProbabilityService enBigramsDistribution;

    @Autowired
    private JedisPool jedisPool;

    @Override
    double conditionalProbability(String word, String prev) {
        Jedis jedis = jedisPool.getResource();
        if (jedis.hexists(EN_BIGRAM, prev + " " + word) && jedis.hexists(EN_UNIGRAM, prev)) {
            final var prob = (double) Long.parseLong(jedis.hget(EN_BIGRAM, prev + " " + word)) / Long.parseLong(jedis.hget(EN_UNIGRAM, prev));
            jedis.close();
            return prob;
        } else {
            jedis.close();
           return enUnigramsDistribution.getGramProbability(word);
        }

        /*if (enBigramsDistribution.getData().containsKey(prev + " " + word) && enUnigramsDistribution.getData().get(prev) != null) {
            return (double)enBigramsDistribution.getData().get(prev + " " + word) / enUnigramsDistribution.getData().get(prev);
        } else {
            return enUnigramsDistribution.getGramProbabilityMap(word);
        }*/
    }
}
