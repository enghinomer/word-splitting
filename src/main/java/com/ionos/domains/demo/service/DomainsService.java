package com.ionos.domains.demo.service;

import com.ionos.domains.demo.model.Candidate;
import org.springframework.beans.factory.ListableBeanFactoryExtensionsKt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DomainsService {
    private final List<String> ccTlds = Arrays.asList("de", "fr", "es", "it", "uk", "co.uk");

    @Autowired
    private SimilarityService similarityService;

    @Autowired
    private LanguageDetectionService languageDetectionService;

    public String getDomainName(String domain) {
        return domain.substring(0, domain.indexOf("."));
    }

    public String getTld(String domain) {
        return domain.substring(domain.indexOf(".")+1);
    }

    public List<String> getDomains(String domainName) {
        final var tld = getTld(domainName);
        if (isCCTld(tld)) {
            return queryDomains(tld);
        }
        return queryDomains(null);
    }

    public List<String> getLevenshteinSimilarityBasedDomains(String domainName) {
        String domainWithoutTld = getDomainName(domainName);
        Map<String, Double> levenshteinSimilarities = new HashMap<>();
        for (String domain : getDomains(domainName)) {
            levenshteinSimilarities.put(domain,
                    similarityService.getLevenshteinSimilarity(domainWithoutTld, getDomainName(domain)));
        }
        return new ArrayList<>(sortByValue(levenshteinSimilarities).keySet());
    }

    // TODO: 27.01.2021 Consider tld
    public List<String> getWordEmbeddingsSimilarityBasedDomains(String domainName) {
        String domainWithoutTld = getDomainName(domainName);
        String domainTld = getTld(domainName);
        Candidate referenceCandidate = languageDetectionService.getBestCandidate(domainWithoutTld);
        Map<String, Double> embeddingsSimilarities = new HashMap<>();
        for (String domain : getDomains(domainName)) {
            System.out.println(domain);
            String tld = getTld(domain);
            double tldSimilarity = getTldsSimilarities(domainTld, tld);
            Candidate candidate = languageDetectionService.getBestCandidate(getDomainName(domain));
            double domainsSimilarity = similarityService.getDomainsEmbeddingsSimilarity(referenceCandidate, candidate);
            double totalSimilarity = domainsSimilarity + 0.15*tldSimilarity;
            embeddingsSimilarities.put(domain, totalSimilarity);
        }
        return new ArrayList<>(sortByValue(embeddingsSimilarities).keySet());
    }

    private double getTldsSimilarities(String tld1, String tld2) {
        return 0;
    }

    private List<String> queryDomains(String tld) {
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

    private boolean isCCTld(String tld) {
        return ccTlds.contains(tld);
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
}
