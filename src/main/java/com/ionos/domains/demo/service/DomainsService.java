package com.ionos.domains.demo.service;

import com.ionos.domains.demo.client.JedisClient;
import com.ionos.domains.demo.model.Candidate;
import com.ionos.domains.demo.model.Domain;
import com.ionos.domains.demo.model.Language;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

@Service
public class DomainsService {
    private final List<String> ccTlds = Arrays.asList("de", "fr", "es", "it", "uk", "co.uk");

    @Autowired
    private SimilarityService similarityService;

    @Autowired
    private LanguageDetectionService languageDetectionService;

    @Autowired
    private JedisClient jedisClient;

    @Autowired
    @Qualifier("fixedThreadPool")
    private ExecutorService executorService;

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

    public List<Domain> getSegmentedDomains(String domainName) {
        final var tld = getTld(domainName);
        if (isCCTld(tld)) {
            return querySegmentedDomains(tld);
        }
        return querySegmentedDomains(null);
    }

    public List<String> getLevenshteinSimilarityBasedDomains(String domainName) {
        String domainWithoutTld = getDomainName(domainName);
        Language language = languageDetectionService.getBestCandidate(domainWithoutTld).getLanguage();
        Map<String, Double> levenshteinSimilarities = new HashMap<>();
        for (String domain : jedisClient.getDomainsFromRedis(language)) {
            levenshteinSimilarities.put(domain,
                    similarityService.getLevenshteinSimilarity(domainWithoutTld, getDomainName(domain)));
        }
        return new ArrayList<>(sortByValue(levenshteinSimilarities).keySet());
    }

    public List<String> getWordEmbeddingsSimilarityBasedDomains(String domainName) {
        String domainWithoutTld = getDomainName(domainName);
        String domainTld = getTld(domainName);
        Candidate referenceCandidate = languageDetectionService.getBestCandidate(domainWithoutTld);
        Map<String, Double> embeddingsSimilarities = new HashMap<>();
        for (String domain : getDomains(domainName)) {
            System.out.println(domain);
            String tld = getTld(domain);
            double tldSimilarity = similarityService.getTldsSimilarity(domainTld, tld);
            Candidate candidate = languageDetectionService.getBestCandidate(getDomainName(domain));
            double domainsSimilarity = similarityService.getCandidatesEmbeddingsSimilarity(referenceCandidate, candidate);
            double totalSimilarity = domainsSimilarity + 0.1*tldSimilarity;
            embeddingsSimilarities.put(domain, totalSimilarity);
        }
        return new ArrayList<>(sortByValue(embeddingsSimilarities).keySet());
    }

    public List<String> getWordEmbeddingsSimilaritySegmentedDomains(String domainName) throws ExecutionException, InterruptedException {
        String domainWithoutTld = getDomainName(domainName);
        String domainTld = getTld(domainName);
        Candidate referenceCandidate = languageDetectionService.getBestCandidate(domainWithoutTld);
        Domain referenceDomain = new Domain();
        referenceDomain.setDomainName(domainName);
        referenceDomain.setKeyWords(referenceCandidate.getWords());
        referenceDomain.setTld(domainTld);
        referenceDomain.setLanguage(referenceCandidate.getLanguage());
        referenceDomain.setWordEmbeddings(jedisClient.getWordEmbeddings(referenceDomain.getKeyWords(), referenceDomain.getLanguage()));
        Map<String, Double> embeddingsSimilarities = new ConcurrentHashMap<>();
       /* for (Domain domain : getSegmentedDomains(domainName)) {
            System.out.println(domain.getKeyWords().get(0));
            String tld = domain.getTld();
            double tldSimilarity = similarityService.getTldsSimilarity(domainTld, tld);
            //Candidate candidate = languageDetectionService.getBestCandidate(getDomainName(domain));
            double domainsSimilarity = similarityService.getDomainsEmbeddingsSimilarity(referenceDomain, domain);
            double totalSimilarity = domainsSimilarity + 0.1*tldSimilarity;
            embeddingsSimilarities.put(StringUtils.join(domain.getKeyWords(), ""), totalSimilarity);
        }*/

        List<Future<?>> futures = new ArrayList<>();
        for (Domain domain : getSegmentedDomains(referenceDomain.getLanguage())) {
            Future<?> future = executorService.submit(() -> embeddingsSimilarities.put(domain.getDomainName(),
                    computeDomainSimilarities(domain, referenceDomain)));
            futures.add(future);
        }
        for (Future<?> future : futures) {
            future.get();
        }
        System.out.println("done");
        return new ArrayList<>(sortByValue(embeddingsSimilarities).keySet());
    }

    private double computeDomainSimilarities(Domain domain, Domain referenceDomain) {
        System.out.println(domain.getDomainName());
        String tld = domain.getTld();
        double tldSimilarity = similarityService.getTldsSimilarity(referenceDomain.getTld(), tld);
        //double domainsSimilarity = similarityService.getEmbeddingsSimilarity(referenceDomain, domain);
        double domainsSimilarity = similarityService.getSIFsimilarity(referenceDomain, domain);
        double totalSimilarity = domainsSimilarity + 0.1*tldSimilarity;
        return totalSimilarity;
    }

    private List<String> queryDomains(String tld) {
        List<String> domains = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("/home/enghin/Documents/Tasks/ENdomains.txt"))) {
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

    private List<Domain> querySegmentedDomains(String tld) {
        List<Domain> domains = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("/home/enghin/Documents/Tasks/domainsUKWords.txt"))) {
            String line = reader.readLine();
            while (line != null) {
                Domain domain = new Domain();
                domain.setKeyWords(Arrays.asList(line.trim().split(" ")));
                domain.setLanguage(Language.EN);
                domain.setTld("uk");
                domain.setDomainName(StringUtils.join(domain.getKeyWords(), ""));
                domains.add(domain);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return domains;
    }

    private List<Domain> getSegmentedDomains(Language language) {
        List<Domain> domains = new ArrayList<>();
        Map<String, String> segmentedDomains = jedisClient.getSegmentedDomainsFromRedis(language);
        for (Map.Entry<String, String> segmentedDomain : segmentedDomains.entrySet()) {
            Domain domain = new Domain();
            domain.setDomainName(this.getDomainName(segmentedDomain.getKey()));
            domain.setTld(this.getTld(segmentedDomain.getKey()));
            domain.setKeyWords(Arrays.asList(segmentedDomain.getValue().split(" ")));
            domain.setLanguage(language);
            domain.setWordEmbeddings(jedisClient.getWordEmbeddings(domain.getKeyWords(), language));
            domains.add(domain);
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
