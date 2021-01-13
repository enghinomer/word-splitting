package com.ionos.domains.demo.resource;

import com.ionos.domains.demo.client.GoogleBooksClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class NamesResource {

    @Autowired
    private GoogleBooksClient googleBooksClient;

    @GetMapping
    public Mono<Object> getNames() {
        return googleBooksClient.getNgramStats();
    }
}
