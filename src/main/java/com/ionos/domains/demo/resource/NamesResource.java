package com.ionos.domains.demo.resource;

import com.ionos.domains.demo.client.GoogleBooksClient;
import com.ionos.domains.demo.model.Candidate;
import com.ionos.domains.demo.service.EnSegmentationService;
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

    @GetMapping
    public Mono<Object> getNames() {
        jedis.set("myKey", "myValue");
        String cachedResponse = jedis.get("myKey");
        Candidate candidate = enSegmentationService.segment("iwanttorun");
        double similarity = enWordsEmbeddings.getCosSimilarity("sport", "running");
        return googleBooksClient.getNgramStats();
    }
}
