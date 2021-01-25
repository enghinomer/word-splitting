package com.ionos.domains.demo.service;

import com.ionos.domains.demo.model.Candidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LanguageDetectionService {

    @Autowired
    private EnSegmentationService enSegmentationService;

    public Candidate getRightCandidate(String domainName) {
        Candidate bestCandidate = enSegmentationService.segment(domainName);
        // Candidate deCandidate = deSegmentationService.segment(domainName);
        //if (deCandidate.getProb > best.getProb) best = deCandidate
        return bestCandidate;
    }
}
