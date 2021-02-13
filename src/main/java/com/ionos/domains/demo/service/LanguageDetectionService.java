package com.ionos.domains.demo.service;

import com.ionos.domains.demo.model.Segmentation;
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

    public Segmentation getBestCandidate(String domainName) {
        Segmentation bestSegmentation = enSegmentationService.segment(domainName);
        bestSegmentation.setLanguage(Language.EN);

        Segmentation deSegmentation = deSegmentationService.segment(domainName);
        if (deSegmentation.getProbability() > bestSegmentation.getProbability()) {
            bestSegmentation = deSegmentation;
            bestSegmentation.setLanguage(Language.DE);
        }

        Segmentation esSegmentation = esSegmentationService.segment(domainName);
        if (esSegmentation.getProbability() > bestSegmentation.getProbability()) {
            bestSegmentation = esSegmentation;
            bestSegmentation.setLanguage(Language.ES);
        }

        Segmentation frSegmentation = frSegmentationService.segment(domainName);
        if (frSegmentation.getProbability() > bestSegmentation.getProbability()) {
            bestSegmentation = frSegmentation;
            bestSegmentation.setLanguage(Language.FR);
        }

        Segmentation itSegmentation = itSegmentationService.segment(domainName);
        if (itSegmentation.getProbability() > bestSegmentation.getProbability()) {
            bestSegmentation = itSegmentation;
            bestSegmentation.setLanguage(Language.IT);
        }
        return bestSegmentation;
    }

    public List<Segmentation> getSegmentations(String domainName) {
        final var text = normString(domainName);
        List<Segmentation> bestSegmentations = enSegmentationService.getAllSegmentations(text);
        for (Segmentation segmentation : bestSegmentations) {
            segmentation.setLanguage(Language.EN);
        }

        List<Segmentation> deSegmentations = deSegmentationService.getAllSegmentations(text);
        if (deSegmentations.get(0).getProbability() > bestSegmentations.get(0).getProbability()) {
            bestSegmentations = deSegmentations;
            for (Segmentation segmentation : bestSegmentations) {
                segmentation.setLanguage(Language.DE);
            }
        }

        List<Segmentation> esSegmentations = esSegmentationService.getAllSegmentations(text);
        if (esSegmentations.get(0).getProbability() > bestSegmentations.get(0).getProbability()) {
            bestSegmentations = esSegmentations;
            for (Segmentation segmentation : bestSegmentations) {
                segmentation.setLanguage(Language.ES);
            }
        }

        List<Segmentation> frSegmentations = frSegmentationService.getAllSegmentations(text);
        if (frSegmentations.get(0).getProbability() > bestSegmentations.get(0).getProbability()) {
            bestSegmentations = frSegmentations;
            for (Segmentation segmentation : bestSegmentations) {
                segmentation.setLanguage(Language.FR);
            }
        }

        List<Segmentation> itSegmentations = itSegmentationService.getAllSegmentations(text);
        if (itSegmentations.get(0).getProbability() > bestSegmentations.get(0).getProbability()) {
            bestSegmentations = itSegmentations;
            for (Segmentation segmentation : bestSegmentations) {
                segmentation.setLanguage(Language.IT);
            }
        }

        for (Segmentation segmentation : bestSegmentations) {
            segmentation.setWords(getSegmentedText(domainName, segmentation.getWords()));
        }

        return bestSegmentations;
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
