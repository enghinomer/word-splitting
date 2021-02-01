package com.ionos.domains.demo.service;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.impl.*;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.io.*;
import java.util.*;

public class SimilarityServiceTemp {

    public static Map<String, Double> wordNetSim = new HashMap<>();
    public static Map<String, Double> lSim = new HashMap<>();
    public static Map<String, Double> embeddingSim = new HashMap<>();

    SegmentationService segmentationService = new SegmentationService();
    WordEmbeddingService wordEmbeddingService = new WordEmbeddingService();

    public SimilarityServiceTemp() throws Exception {

    }

    public double wordNetSimilarities(String word1, String word2) {
        ILexicalDatabase db = new NictWordNet();
        /*RelatednessCalculator[] rcs = {
                new HirstStOnge(db), new LeacockChodorow(db), new Lesk(db),  new WuPalmer(db),
                new Resnik(db), new JiangConrath(db), new Lin(db), new Path(db)
        };*/

        WS4JConfiguration.getInstance().setMFS(true);
        //System.out.println("Similarity between " + word1 + " and " + word2);
        /*for ( RelatednessCalculator rc : rcs ) {
            double s = rc.calcRelatednessOfWords(word1, word2);
            System.out.println( rc.getClass().getName()+"\t"+s );
        }*/
        double sim = new Resnik(db).calcRelatednessOfWords(word1, word2);
        if (sim == Double.MAX_VALUE) return 10;
        return sim;
    }

    public int getLevenshteinDistance(String word1, String word2) {
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
        return levenshteinDistance.apply(word1, word2);
    }

    public double getLevenshteiniSimilarity(String word1, String word2) {
        return 1 - (double) this.getLevenshteinDistance(word1, word2)/(Math.max(word1.length(), word2.length()));
    }

    public double getDomainsWordNetSimilarity(String reference, String domainName2) {
        List<String> words1 = segmentationService.segment(reference).getWords();
        List<String> words2 = segmentationService.segment(domainName2).getWords();

        double wordSimilarity = 0.0;
        for (String w1 : words1) {
            for (String w2 : words2) {
                wordSimilarity += wordNetSimilarities(w1, w2);
            }
        }

        return wordSimilarity /(words1.size() + words2.size());
    }

    public double getDomainsEmbeddingsSimilarity(String reference, String domainName) {
        List<String> words1 = segmentationService.segment(reference).getWords();
        List<String> words2 = segmentationService.segment(domainName).getWords();

        double wordSimilarity = 0.0;
        for (String w1 : words1) {
            for (String w2 : words2) {
                wordSimilarity += wordEmbeddingService.getCosSimilarity(w1, w2);
            }
        }
        return wordSimilarity /(words1.size() + words2.size());
    }

    public double getDomainsWordNetSimilarity(List<String> words1, List<String> words2) {
        double wordSimilarity = 0.0;
        for (String w1 : words1) {
            for (String w2 : words2) {
                wordSimilarity += wordNetSimilarities(w1, w2);
            }
        }

        return wordSimilarity /(words1.size() + words2.size());
    }

    public double getDomainsEmbeddingsSimilarity(List<String> words1, List<String> words2) {
        double wordSimilarity = 0.0;
        for (String w1 : words1) {
            for (String w2 : words2) {
                wordSimilarity += wordEmbeddingService.getCosSimilarity(w1, w2);
            }
        }
        return wordSimilarity /(words1.size() + words2.size());
    }

    public double getLevenshteiniSimilarity(List<String> word1, List<String> word2) {

        final var w1 = String.join("", word1);
        final var w2 = String.join("", word2);
        return 1 - (double) this.getLevenshteinDistance(w1, w2)
                /(Math.max(w1.length(), w2.length()));
    }

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());

        Map<K, V> result = new LinkedHashMap<>();
        for (int i = list.size()-1; i>=0; i--) {
            result.put(list.get(i).getKey(), list.get(i).getValue());
        }

        return result;
    }

    public static void main(String[] args) throws Exception {
        String referenceWord = "naturalplants";
        SimilarityServiceTemp similarityServiceTemp = new SimilarityServiceTemp();

        getSimilarities(referenceWord, similarityServiceTemp);

        similarityServiceTemp.wordNetSimilarities("dog", "soul");

        System.out.println(similarityServiceTemp.getLevenshteiniSimilarity("cat", "dog"));
        System.out.println(similarityServiceTemp.getLevenshteiniSimilarity("cat", "at"));
    }

    private static void getSimilarities(String referenceWord, SimilarityServiceTemp similarityServiceTemp) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("/home/enghin/Documents/Tasks/domainsNameUK.txt"))) {
            String line = reader.readLine();
            while (line != null) {
                line = line.trim();
                System.out.println(line);
                //wordNetSim.put(line, similarityServiceTemp.getDomainsWordNetSimilarity(referenceWord, line));
                //lSim.put(line, similarityServiceTemp.getLevenshteiniSimilarity(referenceWord, line));
                embeddingSim.put(line, similarityServiceTemp.getDomainsEmbeddingsSimilarity(referenceWord, line));
                line = reader.readLine();
            }
        }
        wordNetSim = similarityServiceTemp.sortByValue(wordNetSim);
        embeddingSim = similarityServiceTemp.sortByValue(embeddingSim);
        lSim = similarityServiceTemp.sortByValue(lSim);
    }
}
