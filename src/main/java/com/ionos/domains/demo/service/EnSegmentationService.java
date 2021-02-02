package com.ionos.domains.demo.service;

import com.ionos.domains.demo.model.Candidate;
import com.ionos.domains.demo.model.Language;
import com.ionos.domains.demo.model.ProbDistribution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import static com.ionos.domains.demo.configuration.ApplicationConfiguration.EN_BIGRAM;
import static com.ionos.domains.demo.configuration.ApplicationConfiguration.EN_UNIGRAM;

@Service
public class EnSegmentationService extends AbstractSegmentationService {

    @Autowired
    private ProbabilityService enUnigramsDistribution;

    @Autowired
    private ProbabilityService enBigramsDistribution;

    //@Autowired
    //private Jedis jedis;

    @Override
    double conditionalProbability(String word, String prev) {
        /*if (jedis.hexists(EN_BIGRAM, prev + " " + word) && jedis.hexists(EN_UNIGRAM, prev)) {
            return (double) Long.parseLong(jedis.hget(EN_BIGRAM, prev + " " + word)) / Long.parseLong(jedis.hget(EN_UNIGRAM, prev));
        } else {
           return enUnigramsDistribution.getGramProbability(word);
        }*/
        if (enBigramsDistribution.getData().containsKey(prev + " " + word) && enUnigramsDistribution.getData().get(prev) != null) {
            return (double)enBigramsDistribution.getData().get(prev + " " + word) / enUnigramsDistribution.getData().get(prev);
        } else {
            return enUnigramsDistribution.getGramProbabilityMap(word);
        }
    }
}
