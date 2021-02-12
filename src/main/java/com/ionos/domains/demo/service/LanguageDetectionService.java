package com.ionos.domains.demo.service;

import com.ionos.domains.demo.model.Candidate;
import com.ionos.domains.demo.model.Language;
import com.ionos.domains.demo.service.segmentation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        final var text = normString(domainName);
        List<Candidate> bestCandidates = enSegmentationService.getAllCandidates(text);
        for (Candidate candidate : bestCandidates) {
            candidate.setLanguage(Language.EN);
        }

        List<Candidate> deCandidates = deSegmentationService.getAllCandidates(text);
        if (deCandidates.get(0).getProbability() > bestCandidates.get(0).getProbability()) {
            bestCandidates = deCandidates;
            for (Candidate candidate : bestCandidates) {
                candidate.setLanguage(Language.DE);
            }
        }

        List<Candidate> esCandidates = esSegmentationService.getAllCandidates(text);
        if (esCandidates.get(0).getProbability() > bestCandidates.get(0).getProbability()) {
            bestCandidates = esCandidates;
            for (Candidate candidate : bestCandidates) {
                candidate.setLanguage(Language.ES);
            }
        }

        List<Candidate> frCandidates = frSegmentationService.getAllCandidates(text);
        if (frCandidates.get(0).getProbability() > bestCandidates.get(0).getProbability()) {
            bestCandidates = frCandidates;
            for (Candidate candidate : bestCandidates) {
                candidate.setLanguage(Language.FR);
            }
        }

        List<Candidate> itCandidates = itSegmentationService.getAllCandidates(text);
        if (itCandidates.get(0).getProbability() > bestCandidates.get(0).getProbability()) {
            bestCandidates = itCandidates;
            for (Candidate candidate : bestCandidates) {
                candidate.setLanguage(Language.IT);
            }
        }

        for (Candidate candidate : bestCandidates) {
            candidate.setWords(getSegmentedText(domainName, candidate.getWords()));
        }

        return bestCandidates;
    }

    private String normString(String text) {
        List<String> parts = new ArrayList<>();
        boolean isCharSequence = true;
        String part = "";
        String x = "";
        for (int i =0; i < text.length(); i++) {
            if (!Character.isLetter(text.charAt(i))) {
                if (isCharSequence) {
                    x += "*";
                }
                part += text.charAt(i);
                isCharSequence = false;
            }
            if (Character.isLetter(text.charAt(i))) {
                if (!part.equals("")) {
                    parts.add(part);
                }
                part = "";
                x += text.charAt(i);
                isCharSequence = true;
            }
            if (i == text.length()-1 && !part.equals("")) {
                parts.add(part);
            }
        }
        return x;
    }

    private List<String> getSegmentedText(String text, List<String> segments) {
        List<String> words = new ArrayList<>();
        List<String> parts = new ArrayList<>();
        boolean isCharSequence = true;
        String part = "";
        String x = "";
        for (int i =0; i < text.length(); i++) {
            if (!Character.isLetter(text.charAt(i))) {
                if (isCharSequence) {
                    x += "*";
                }
                part += text.charAt(i);
                isCharSequence = false;
            }
            if (Character.isLetter(text.charAt(i))) {
                if (!part.equals("")) {
                    parts.add(part);
                }
                part = "";
                x += text.charAt(i);
                isCharSequence = true;
            }
            if (i == text.length()-1 && !part.equals("")) {
                parts.add(part);
            }
        }

        int j = 0;
        for (String w : segments) {
            if (w.equals("*")) {
                words.add(parts.get(j));
                j++;
            } else {
                words.add(w);
            }
        }
        return words;
    }
}
