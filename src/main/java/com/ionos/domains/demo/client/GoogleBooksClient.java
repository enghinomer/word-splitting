package com.ionos.domains.demo.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class GoogleBooksClient {

    @Autowired
    public WebClient googleNgramsWebClient;

    public Mono<Object> getNgramStats() {
        return googleNgramsWebClient
                .get()
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class);
    }
}
