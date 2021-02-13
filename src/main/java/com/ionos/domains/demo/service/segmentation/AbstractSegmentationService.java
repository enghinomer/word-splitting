package com.ionos.domains.demo.service.segmentation;

import com.ionos.domains.demo.model.Segmentation;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public abstract class AbstractSegmentationService {

    private final int longestWord = 20;

    private Map<String, Segmentation> terms = new HashMap<>();
    List<Segmentation> lastSegmentations = new ArrayList<>();

    public Segmentation segment(String text, String prev) {
        if (StringUtils.isEmpty(prev)) {
            prev = "<S>";
        }
        if (StringUtils.isEmpty(text)) {
            return new Segmentation(0.0, new ArrayList<>());
        }

        List<Segmentation> segmentations = new ArrayList<>();
        for (List<String> split : this.getSplits(text)) {
            String first = split.get(0);
            String remaining = split.get(1);
            if (!terms.containsKey(remaining + " " + first)) {
                terms.put(remaining + " " + first, segment(remaining, first));
            }
            segmentations.add(combine(new Segmentation(Math.log10(conditionalProbability(first, prev)), Collections.singletonList(first)),
                    terms.get(remaining + " " + first)));
        }

        /*for (Candidate candidate : candidates) {
            System.out.println(candidate.getProbability() + " " + Arrays.toString(candidate.getWords().toArray()));
        }*/

        lastSegmentations = segmentations;
        //System.out.println("==================");
        return max(segmentations);
    }

    private Segmentation max(List<Segmentation> segmentations) {
        if (segmentations.isEmpty()) {
            return null;
        }
        Segmentation maxSegmentation = segmentations.get(0);
        for (Segmentation segmentation : segmentations) {
            if (segmentation.getProbability() > maxSegmentation.getProbability()) {
                maxSegmentation = segmentation;
            }
        }
        return maxSegmentation;
    }

    //Return a list of all possible splits
    private List<List<String>> getSplits(String text) {
        List<List<String>> splits = new ArrayList<>();
        for (int i=0; i<Math.min(text.length(), longestWord); i++) {
            List<String> split = new ArrayList<>();
            split.add(text.substring(0, i+1));
            split.add(text.substring(i+1));
            splits.add(split);
        }
        return splits;
    }

    //Combine first and rem results into one (probability, words) pair
    private Segmentation combine(Segmentation first, Segmentation remaining) {
        return new Segmentation(first.getProbability() + remaining.getProbability(),
                Stream.concat(first.getWords().stream(), remaining.getWords().stream())
                        .collect(Collectors.toList()));
    }

    public Segmentation segment(String text) {
        Segmentation segmentation = this.segment(text, null);
        return segmentation;
    }

    public List<Segmentation> getAllSegmentations(String text) {
        segment(text);
        Collections.sort(this.lastSegmentations);
        return this.lastSegmentations;
    }

    abstract double conditionalProbability(String word, String prev);
}
