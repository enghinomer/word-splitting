package com.ionos.domains.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItSegmentationService extends AbstractSegmentationService {

    @Autowired
    private ProbabilityService itUnigramsDistribution;

    @Autowired
    private ProbabilityService itBigramsDistribution;

    @Override
    double conditionalProbability(String word, String prev) {
        if (itBigramsDistribution.getData().containsKey(prev + " " + word) && itUnigramsDistribution.getData().get(prev) != null) {
            return (double)itBigramsDistribution.getData().get(prev + " " + word) / itUnigramsDistribution.getData().get(prev);
        } else {
            return itUnigramsDistribution.getGramProbabilityMap(word);
        }
    }
}
