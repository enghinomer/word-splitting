package com.ionos.domains.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FrSegmentationService extends AbstractSegmentationService {

    @Autowired
    private ProbabilityService frUnigramsDistribution;

    @Autowired
    private ProbabilityService frBigramsDistribution;

    @Override
    double conditionalProbability(String word, String prev) {
        if (frBigramsDistribution.getData().containsKey(prev + " " + word) && frUnigramsDistribution.getData().get(prev) != null) {
            return (double)frBigramsDistribution.getData().get(prev + " " + word) / frUnigramsDistribution.getData().get(prev);
        } else {
            return frUnigramsDistribution.getGramProbabilityMap(word);
        }
    }
}
