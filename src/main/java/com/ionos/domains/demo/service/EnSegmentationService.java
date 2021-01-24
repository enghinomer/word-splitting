package com.ionos.domains.demo.service;

import com.ionos.domains.demo.model.Candidate;
import com.ionos.domains.demo.model.ProbDistribution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EnSegmentationService extends AbstractSegmentationService {

    @Autowired
    private ProbDistribution enUnigramsDistribution;

    @Autowired
    private ProbDistribution enBigramsDistribution;

    @Override
    double conditionalProbability(String word, String prev) {
        if (enBigramsDistribution.getData().containsKey(prev + " " + word) && enUnigramsDistribution.getData().get(prev) != null) {
            return (double)enBigramsDistribution.getData().get(prev + " " + word) / enUnigramsDistribution.getData().get(prev);
        } else {
            return enUnigramsDistribution.getGramProbability(word);
        }
    }
}
