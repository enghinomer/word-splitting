package com.ionos.domains.demo.service.segmentation;

import com.ionos.domains.demo.model.Candidate;
import com.ionos.domains.demo.model.Language;
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

    private Map<String, Candidate> terms = new HashMap<>();
    List<Candidate> lastCandidates = new ArrayList<>();

    public Candidate segment(String text, String prev) {
        if (StringUtils.isEmpty(prev)) {
            prev = "<S>";
        }
        if (StringUtils.isEmpty(text)) {
            return new Candidate(0.0, new ArrayList<>());
        }

        List<Candidate> candidates = new ArrayList<>();
        for (List<String> split : this.getSplits(text)) {
            String first = split.get(0);
            String remaining = split.get(1);
            if (!terms.containsKey(remaining + " " + first)) {
                terms.put(remaining + " " + first, segment(remaining, first));
            }
            candidates.add(combine(new Candidate(Math.log10(conditionalProbability(first, prev)), Collections.singletonList(first)),
                    terms.get(remaining + " " + first)));
        }

        /*for (Candidate candidate : candidates) {
            System.out.println(candidate.getProbability() + " " + Arrays.toString(candidate.getWords().toArray()));
        }*/

        lastCandidates = candidates;
        //System.out.println("==================");
        return max(candidates);
    }

    private Candidate max(List<Candidate> candidates) {
        if (candidates.isEmpty()) {
            return null;
        }
        Candidate maxCandidate = candidates.get(0);
        for (Candidate candidate : candidates) {
            if (candidate.getProbability() > maxCandidate.getProbability()) {
                maxCandidate = candidate;
            }
        }
        return maxCandidate;
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
    private Candidate combine(Candidate first, Candidate remaining) {
        return new Candidate(first.getProbability() + remaining.getProbability(),
                Stream.concat(first.getWords().stream(), remaining.getWords().stream())
                        .collect(Collectors.toList()));
    }

    public Candidate segment(String text) {
        Candidate candidate = this.segment(text, null);
        return candidate;
    }

    public List<Candidate> getAllCandidates(String text) {
        segment(text);
        Collections.sort(this.lastCandidates);
        return this.lastCandidates;
    }

    abstract double conditionalProbability(String word, String prev);
}
