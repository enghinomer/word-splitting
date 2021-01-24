package com.ionos.domains.demo.resource;

import com.ionos.domains.demo.client.GoogleBooksClient;
import com.ionos.domains.demo.model.Candidate;
import com.ionos.domains.demo.service.EnSegmentationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class NamesResource {

    @Autowired
    private GoogleBooksClient googleBooksClient;

    @Autowired
    private EnSegmentationService enSegmentationService;

    @GetMapping
    public Mono<Object> getNames() {
        Candidate candidate = enSegmentationService.segment("iwanttorun");
        return googleBooksClient.getNgramStats();
    }
}
