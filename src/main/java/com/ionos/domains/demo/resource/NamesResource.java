package com.ionos.domains.demo.resource;

import com.ionos.domains.demo.client.GoogleBooksClient;
import com.ionos.domains.demo.model.Candidate;
import com.ionos.domains.demo.model.Language;
import com.ionos.domains.demo.model.SimilarDomains;
import com.ionos.domains.demo.service.*;
import com.ionos.domains.demo.service.segmentation.EnSegmentationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class NamesResource {

    @Autowired
    private GoogleBooksClient googleBooksClient;

    @Autowired
    private EnSegmentationService enSegmentationService;

    @Autowired
    private WordsEmbeddingsService enWordsEmbeddings;

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private SimilarityService similarityService;

    @Autowired
    private LanguageDetectionService languageDetectionService;

    @Autowired
    private DomainsService domainsService;

    @GetMapping
    public Mono<Object> getNames() throws IOException {
        Jedis jedis = jedisPool.getResource();
        int i = 0;
        for (String name : domainsService.getDomains("grandmaster.uk")) {
            if (i%6 ==0) {
                String domainName = domainsService.getDomainName(name);
                Candidate candidateBest = languageDetectionService.getBestCandidate(domainName);
                if (Language.DE.equals(candidateBest.getLanguage())) {
                    System.out.println(name + " " + String.join(" ", candidateBest.getWords()));
                    jedis.hset("Domain-DE", domainName, String.join(" ", candidateBest.getWords()));
                }
                //double sim = similarityService.getDomainsEmbeddingsSimilarity(candidate1, candidate2);
                //double simLevenstain = similarityService.getLevenshteinSimilarity("iwinbecauseican", "iwanttorun");

            }
            i++;
        }
        jedis.close();
        return googleBooksClient.getNgramStats();
    }

    @GetMapping(value = "segmentation")
    public Mono<Object> getSegmentation(@RequestParam(defaultValue = "") String text,
                                        @RequestParam(defaultValue = "1") int limit) {
        int limitValue = limit <= 0 ? Integer.parseInt("1") : limit;
        text = text.toLowerCase();
        String domainName = domainsService.getDomainName(text);
        String tld = domainsService.getTld(text);
        List<Candidate> candidates = languageDetectionService.getCandidates(domainName);
        return Mono.just(candidates.subList(0, limitValue));
    }

    @GetMapping(value = "similarity")
    public Mono<Object> getSimilarities(@RequestParam(defaultValue = "") String text,
                                        @RequestParam(defaultValue = "10") int limit) throws ExecutionException, InterruptedException {
        int limitValue = limit <= 0 ? Integer.parseInt("10") : limit;
        SimilarDomains similarDomains = new SimilarDomains();
        similarDomains.setLevenshteinDomains(domainsService.getLevenshteinSimilarityBasedDomains(text).subList(0, limitValue));
        similarDomains.setEmbeddingsDomains(domainsService.getWordEmbeddingsSimilaritySegmentedDomains(text).subList(0, limitValue));
        return Mono.just(similarDomains);
    }
}
