package com.ionos.domains.demo.resource;

import com.ionos.domains.demo.client.GoogleBooksClient;
import com.ionos.domains.demo.model.Candidate;
import com.ionos.domains.demo.model.SimilarDomains;
import com.ionos.domains.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import redis.clients.jedis.Jedis;

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

    //@Autowired
    //private Jedis jedis;

    @Autowired
    private SimilarityService similarityService;

    @Autowired
    private LanguageDetectionService languageDetectionService;

    @Autowired
    private DomainsService domainsService;

    @GetMapping
    public Mono<Object> getNames() throws IOException {
        //jedis.set("myKey", "myValue");
        //String cachedResponse = jedis.get("myKey");
        Candidate candidate = enSegmentationService.segment("iwanttorun");
        double similarity = enWordsEmbeddings.getCosSimilarity("sport", "running");
        Candidate candidate1 = languageDetectionService.getBestCandidate("iwinbecauseican");
        Candidate candidate2 = languageDetectionService.getBestCandidate("iwanttorun");
        for (String name : domainsService.getDomains("grandmaster.uk")) {
            System.out.println(name);
            languageDetectionService.getBestCandidate(name);
            //double sim = similarityService.getDomainsEmbeddingsSimilarity(candidate1, candidate2);
            //double simLevenstain = similarityService.getLevenshteinSimilarity("iwinbecauseican", "iwanttorun");

        }
        return googleBooksClient.getNgramStats();
    }

    @GetMapping(value = "segmentation")
    public Mono<Object> getSegmentation(@RequestParam(defaultValue = "") String text,
                                        @RequestParam(defaultValue = "1") int limit) {
        int limitValue = limit <= 0 ? Integer.parseInt("1") : limit;
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
