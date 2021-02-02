package com.ionos.domains.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
public class DeSegmentationService extends AbstractSegmentationService {

    @Autowired
    private ProbabilityService deUnigramsDistribution;

    @Autowired
    private ProbabilityService deBigramsDistribution;

    //@Autowired
    //private Jedis jedis;

    @Override
    double conditionalProbability(String word, String prev) {
        if (deBigramsDistribution.getData().containsKey(prev + " " + word) && deUnigramsDistribution.getData().get(prev) != null) {
            return (double)deBigramsDistribution.getData().get(prev + " " + word) / deUnigramsDistribution.getData().get(prev);
        } else {
            return deUnigramsDistribution.getGramProbabilityMap(word);
        }
    }
}
