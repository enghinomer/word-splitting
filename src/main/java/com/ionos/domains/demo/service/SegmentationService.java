package com.ionos.domains.demo.service;

import com.ionos.domains.demo.model.Candidate;
import com.ionos.domains.demo.model.Language;
import com.ionos.domains.demo.model.ProbDistribution;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SegmentationService {

    private ProbDistribution unigramDistributionEN;
    private ProbDistribution bigramDistributionEN;
    private ProbDistribution unigramDistributionDE;
    private ProbDistribution bigramDistributionDE;
    private ProbDistribution unigramDistributionES;
    private ProbDistribution bigramDistributionES;
    private ProbDistribution unigramDistributionFR;
    private ProbDistribution bigramDistributionFR;
    private ProbDistribution unigramDistributionIT;
    private ProbDistribution bigramDistributionIT;
    private Map<String, Candidate> terms = new HashMap<>();
    List<Candidate> lastCandidates = new ArrayList<>();

    private final int longestWord = 20;

    public SegmentationService() throws IOException {
        unigramDistributionEN = new ProbDistribution("datasets/new/en-unigrams.txt", 1708180445L);
        bigramDistributionEN = new ProbDistribution("datasets/new/en-bigrams.txt", 1708180445L);

        unigramDistributionDE = new ProbDistribution("datasets/new/de-unigrams.txt", 2562270667L);
        bigramDistributionDE = new ProbDistribution("datasets/new/de-bigrams.txt", 2562270667L);

        unigramDistributionES = new ProbDistribution("datasets/new/es-unigrams.txt", 1249309182L);
        bigramDistributionES = new ProbDistribution("datasets/new/es-bigrams.txt", 1249309182L);

        unigramDistributionFR = new ProbDistribution("datasets/new/fr-unigrams.txt", 614851502L);
        bigramDistributionFR = new ProbDistribution("datasets/new/fr-bigrams.txt", 614851502L);

        unigramDistributionIT = new ProbDistribution("datasets/new/it-unigrams.txt", 4178523593L);
        bigramDistributionIT = new ProbDistribution("datasets/new/it-bigrams.txt", 4178523593L);
    }

    public Candidate segmentEN(String text, String prev) {
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
                terms.put(remaining + " " + first, segmentEN(remaining, first));
            }
            candidates.add(combine(new Candidate(Math.log10(conditionalProbabilityEN(first, prev)), Collections.singletonList(first)),
                    terms.get(remaining + " " + first)));
        }

        /*for (Candidate candidate : candidates) {
            System.out.println(candidate.getProbability() + " " + Arrays.toString(candidate.getWords().toArray()));
        }*/

        lastCandidates = candidates;
        //System.out.println("==================");
        return max(candidates);
    }

    public Candidate segmentDE(String text, String prev) {
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
                terms.put(remaining + " " + first, segmentDE(remaining, first));
            }
            candidates.add(combine(new Candidate(Math.log10(conditionalProbabilityDE(first, prev)), Collections.singletonList(first)),
                    terms.get(remaining + " " + first)));
        }

        /*for (Candidate candidate : candidates) {
            System.out.println(candidate.getProbability() + " " + Arrays.toString(candidate.getWords().toArray()));
        }*/

        lastCandidates = candidates;
        //System.out.println("==================");
        return max(candidates);
    }

    public Candidate segmentES(String text, String prev) {
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
                terms.put(remaining + " " + first, segmentES(remaining, first));
            }
            candidates.add(combine(new Candidate(Math.log10(conditionalProbabilityES(first, prev)), Collections.singletonList(first)),
                    terms.get(remaining + " " + first)));
        }

        /*for (Candidate candidate : candidates) {
            System.out.println(candidate.getProbability() + " " + Arrays.toString(candidate.getWords().toArray()));
        }*/

        lastCandidates = candidates;
        //System.out.println("==================");
        return max(candidates);
    }

    public Candidate segmentFR(String text, String prev) {
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
                terms.put(remaining + " " + first, segmentFR(remaining, first));
            }
            candidates.add(combine(new Candidate(Math.log10(conditionalProbabilityFR(first, prev)), Collections.singletonList(first)),
                    terms.get(remaining + " " + first)));
        }

        /*for (Candidate candidate : candidates) {
            System.out.println(candidate.getProbability() + " " + Arrays.toString(candidate.getWords().toArray()));
        }*/

        lastCandidates = candidates;
        //System.out.println("==================");
        return max(candidates);
    }

    public Candidate segmentIT(String text, String prev) {
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
                terms.put(remaining + " " + first, segmentIT(remaining, first));
            }
            candidates.add(combine(new Candidate(Math.log10(conditionalProbabilityIT(first, prev)), Collections.singletonList(first)),
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

    private double conditionalProbabilityEN(String word, String prev) {
        if (bigramDistributionEN.getData().containsKey(prev + " " + word) && unigramDistributionEN.getData().get(prev) != null) {
            return (double) bigramDistributionEN.getData().get(prev + " " + word) / unigramDistributionEN.getData().get(prev);
        } else {
            return unigramDistributionEN.getGramProbability(word);
        }
    }

    private double conditionalProbabilityDE(String word, String prev) {
        if (bigramDistributionDE.getData().containsKey(prev + " " + word) && unigramDistributionDE.getData().get(prev) != null) {
            return (double) bigramDistributionDE.getData().get(prev + " " + word) / unigramDistributionDE.getData().get(prev);
        } else {
            return unigramDistributionDE.getGramProbability(word);
        }
    }

    private double conditionalProbabilityES(String word, String prev) {
        if (bigramDistributionES.getData().containsKey(prev + " " + word) && unigramDistributionES.getData().get(prev) != null) {
            return (double) bigramDistributionES.getData().get(prev + " " + word) / unigramDistributionES.getData().get(prev);
        } else {
            return unigramDistributionES.getGramProbability(word);
        }
    }

    private double conditionalProbabilityFR(String word, String prev) {
        if (bigramDistributionFR.getData().containsKey(prev + " " + word) && unigramDistributionFR.getData().get(prev) != null) {
            return (double) bigramDistributionFR.getData().get(prev + " " + word) / unigramDistributionFR.getData().get(prev);
        } else {
            return unigramDistributionFR.getGramProbability(word);
        }
    }

    private double conditionalProbabilityIT(String word, String prev) {
        if (bigramDistributionIT.getData().containsKey(prev + " " + word) && unigramDistributionIT.getData().get(prev) != null) {
            return (double) bigramDistributionIT.getData().get(prev + " " + word) / unigramDistributionIT.getData().get(prev);
        } else {
            return unigramDistributionIT.getGramProbability(word);
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

    public Candidate segmentEN(String text) {
        terms.clear();
        return this.segmentEN(text, null);
    }

    public Candidate segmentDE(String text) {
        terms.clear();
        return this.segmentDE(text, null);
    }

    public Candidate segmentFR(String text) {
        terms.clear();
        return this.segmentFR(text, null);
    }

    public Candidate segmentES(String text) {
        terms.clear();
        return this.segmentES(text, null);
    }

    public Candidate segmentIT(String text) {
        terms.clear();
        return this.segmentIT(text, null);
    }

    private static List<String> queryDomains(String tld) {
        List<String> domains = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("/home/enghin/Documents/Tasks/domainsUK.txt"))) {
            String line = reader.readLine();
            while (line != null) {
                domains.add(line.trim());
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return domains;
    }

    public static void main(String[] args) throws IOException {
        //Jedis jedis = new Jedis();

        SegmentationService segmentationService = new SegmentationService();

        int nr = 0;
        int skip = 0;

String name = "adrk-bg-muenchen.de";
            String domainName = getDomainName(name);

            final var text = normString(domainName);
            Candidate best;
            Candidate candidateEN = segmentationService.segmentEN(text);
            best = candidateEN;
            best.setLanguage(Language.EN);
            Candidate candidateDE = segmentationService.segmentDE(text);
            if (candidateDE.getProbability() > best.getProbability()) {
                best = candidateDE;
                best.setLanguage(Language.DE);
            }
            Candidate candidateES = segmentationService.segmentES(text);
            if (candidateES.getProbability() > best.getProbability()) {
                best = candidateES;
                best.setLanguage(Language.ES);
            }
            Candidate candidateFR = segmentationService.segmentFR(text);
            if (candidateFR.getProbability() > best.getProbability()) {
                best = candidateFR;
                best.setLanguage(Language.FR);
            }
            Candidate candidateIT = segmentationService.segmentIT(text);
            if (candidateIT.getProbability() > best.getProbability()) {
                best = candidateIT;
                best.setLanguage(Language.IT);
            }

                System.out.println(best.getProbability() + " " + best.getLanguage().name());
                System.out.println(Arrays.toString(getSegmentedText(domainName, best.getWords()).toArray()));

                String segmented = org.apache.commons.lang3.StringUtils.join(getSegmentedText(domainName, best.getWords()), " ");
                //jedis.hset("Domain-IT", name, segmented);
                System.out.println(nr);
                nr++;

    }

    private static String normString(String text) {
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

    private static List<String> getSegmentedText(String text, List<String> segments) {
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

    private static List<String> query(String tld) {
        List<String> domains = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("/home/enghin/Documents/Tasks/ITdomains.txt"))) {
            String line = reader.readLine();
            while (line != null) {
                domains.add(line.trim());
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return domains;
    }

    public static String getDomainName(String domain) {
        return domain.substring(0, domain.indexOf("."));
    }
}
