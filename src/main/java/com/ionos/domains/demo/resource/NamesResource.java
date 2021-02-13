package com.ionos.domains.demo.resource;

import com.ionos.domains.demo.model.Segmentation;
import com.ionos.domains.demo.model.SimilarDomains;
import com.ionos.domains.demo.service.*;
import com.ionos.domains.demo.service.segmentation.EnSegmentationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class NamesResource {

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
        return null;
    }

    @GetMapping(value = "segmentation")
    public Mono<Object> getSegmentation(@RequestParam(defaultValue = "") String text,
                                        @RequestParam(defaultValue = "1") int limit) {
        int limitValue = limit <= 0 ? Integer.parseInt("1") : limit;
        text = text.toLowerCase();
        String domainName = domainsService.getDomainName(text);
        List<Segmentation> segmentations = languageDetectionService.getSegmentations(domainName);
        return Mono.just(segmentations.subList(0, limitValue));
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
