package com.ionos.domains.demo.resource;

import com.ionos.domains.demo.client.GoogleBooksClient;
import com.ionos.domains.demo.model.Candidate;
import com.ionos.domains.demo.service.EnSegmentationService;
import com.ionos.domains.demo.service.LanguageDetectionService;
import com.ionos.domains.demo.service.SimilarityService;
import com.ionos.domains.demo.service.WordsEmbeddingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import redis.clients.jedis.Jedis;

@RestController
public class NamesResource {

    @Autowired
    private GoogleBooksClient googleBooksClient;

    @Autowired
    private EnSegmentationService enSegmentationService;

    @Autowired
    private WordsEmbeddingsService enWordsEmbeddings;

    @Autowired
    private Jedis jedis;

    @Autowired
    private SimilarityService similarityService;

    @Autowired
    private LanguageDetectionService languageDetectionService;

    @GetMapping
    public Mono<Object> getNames() {
        jedis.set("myKey", "myValue");
        String cachedResponse = jedis.get("myKey");
        Candidate candidate = enSegmentationService.segment("iwanttorun");
        double similarity = enWordsEmbeddings.getCosSimilarity("sport", "running");
        Candidate candidate1 = languageDetectionService.getRightCandidate("iwinbecauseican");
        Candidate candidate2 = languageDetectionService.getRightCandidate("iwanttorun");
        for (int i = 0; i < 100000; i++) {
            double sim = similarityService.getDomainsEmbeddingsSimilarity(candidate1, candidate2);
            double simLevenstain = similarityService.getLevenshteiniSimilarity("iwinbecauseican", "iwanttorun");
            if (i%1000 == 0) {
                System.out.println(i);
            }
        }
        return googleBooksClient.getNgramStats();
    }
}
