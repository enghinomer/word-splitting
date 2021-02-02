package com.ionos.domains.demo.service;

import com.ionos.domains.demo.model.Candidate;
import com.ionos.domains.demo.model.Language;
import com.ionos.domains.demo.service.segmentation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LanguageDetectionService {

    @Autowired
    private EnSegmentationService enSegmentationService;

    @Autowired
    private DeSegmentationService deSegmentationService;

    @Autowired
    private EsSegmentationService esSegmentationService;

    @Autowired
    private FrSegmentationService frSegmentationService;

    @Autowired
    private ItSegmentationService itSegmentationService;

    public Candidate getBestCandidate(String domainName) {
        Candidate bestCandidate = enSegmentationService.segment(domainName);
        bestCandidate.setLanguage(Language.EN);

        Candidate deCandidate = deSegmentationService.segment(domainName);
        if (deCandidate.getProbability() > bestCandidate.getProbability()) {
            bestCandidate = deCandidate;
            bestCandidate.setLanguage(Language.DE);
        }

        Candidate esCandidate = esSegmentationService.segment(domainName);
        if (esCandidate.getProbability() > bestCandidate.getProbability()) {
            bestCandidate = esCandidate;
            bestCandidate.setLanguage(Language.ES);
        }

        Candidate frCandidate = frSegmentationService.segment(domainName);
        if (frCandidate.getProbability() > bestCandidate.getProbability()) {
            bestCandidate = frCandidate;
            bestCandidate.setLanguage(Language.FR);
        }

        Candidate itCandidate = itSegmentationService.segment(domainName);
        if (itCandidate.getProbability() > bestCandidate.getProbability()) {
            bestCandidate = itCandidate;
            bestCandidate.setLanguage(Language.IT);
        }
        return bestCandidate;
    }

    public List<Candidate> getCandidates(String domainName) {
        List<Candidate> bestCandidates = enSegmentationService.getAllCandidates(domainName);
        for (Candidate candidate : bestCandidates) {
            candidate.setLanguage(Language.EN);
        }

        List<Candidate> deCandidates = deSegmentationService.getAllCandidates(domainName);
        if (deCandidates.get(0).getProbability() > bestCandidates.get(0).getProbability()) {
            bestCandidates = deCandidates;
            for (Candidate candidate : bestCandidates) {
                candidate.setLanguage(Language.DE);
            }
        }

        List<Candidate> esCandidates = esSegmentationService.getAllCandidates(domainName);
        if (esCandidates.get(0).getProbability() > bestCandidates.get(0).getProbability()) {
            bestCandidates = esCandidates;
            for (Candidate candidate : bestCandidates) {
                candidate.setLanguage(Language.ES);
            }
        }

        List<Candidate> frCandidates = frSegmentationService.getAllCandidates(domainName);
        if (frCandidates.get(0).getProbability() > bestCandidates.get(0).getProbability()) {
            bestCandidates = frCandidates;
            for (Candidate candidate : bestCandidates) {
                candidate.setLanguage(Language.FR);
            }
        }

        List<Candidate> itCandidates = itSegmentationService.getAllCandidates(domainName);
        if (itCandidates.get(0).getProbability() > bestCandidates.get(0).getProbability()) {
            bestCandidates = itCandidates;
            for (Candidate candidate : bestCandidates) {
                candidate.setLanguage(Language.IT);
            }
        }
        return bestCandidates;
    }
}
