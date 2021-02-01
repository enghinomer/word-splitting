package com.ionos.domains.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EsSegmentationService extends AbstractSegmentationService {

    @Autowired
    private ProbabilityService esUnigramsDistribution;

    @Autowired
    private ProbabilityService esBigramsDistribution;

    @Override
    double conditionalProbability(String word, String prev) {
        if (esBigramsDistribution.getData().containsKey(prev + " " + word) && esUnigramsDistribution.getData().get(prev) != null) {
            return (double)esBigramsDistribution.getData().get(prev + " " + word) / esUnigramsDistribution.getData().get(prev);
        } else {
            return esUnigramsDistribution.getGramProbabilityMap(word);
        }
    }
}
