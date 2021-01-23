package com.ionos.domains.demo.service;

import com.ionos.domains.demo.model.Candidate;
import com.ionos.domains.demo.model.ProbDistribution;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SegmentationService {

    private ProbDistribution unigramDistribution;
    private ProbDistribution bigramDistribution;
    private Map<String, Candidate> terms = new HashMap<>();
    List<Candidate> lastCandidates = new ArrayList<>();

    private final int longestWord = 20;

    public SegmentationService() throws IOException {
        unigramDistribution = new ProbDistribution("count_1w.txt");
        bigramDistribution = new ProbDistribution("count_2w.txt");
    }

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

    public List<Candidate> getLastCandidates() {
        return this.lastCandidates;
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

    private double conditionalProbability(String word, String prev) {
        if (bigramDistribution.getData().containsKey(prev + " " + word) && unigramDistribution.getData().get(prev) != null) {
            return (double)bigramDistribution.getData().get(prev + " " + word) / unigramDistribution.getData().get(prev);
        } else {
            return unigramDistribution.getGramProbability(word);
        }
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
        return this.segment(text, null);
    }

    public static void main(String[] args) throws IOException {
        SegmentationService segmentationService = new SegmentationService();
        Candidate candidate = segmentationService.segment("thisissparta", null);
        /*for (Candidate c : segmentation.lastCandidates) {
            System.out.println(c.getProbability() + " " + Arrays.toString(c.getWords().toArray()));
        }*/
        System.out.println(candidate.getProbability());
        System.out.println(Arrays.toString(candidate.getWords().toArray()));
    }
}
