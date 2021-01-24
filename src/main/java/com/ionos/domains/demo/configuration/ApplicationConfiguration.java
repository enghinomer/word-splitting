package com.ionos.domains.demo.configuration;

import com.ionos.domains.demo.model.ProbDistribution;
import com.ionos.domains.demo.service.WordsEmbeddingsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public ProbDistribution enUnigramsDistribution() throws IOException {
        return new ProbDistribution("count_1w.txt");
    }

    @Bean
    public ProbDistribution enBigramsDistribution() throws IOException {
        return new ProbDistribution("count_2w.txt");
    }

    @Bean
    public WordsEmbeddingsService enWordsEmbeddings() throws Exception {
        return new WordsEmbeddingsService("/home/enghin/DataSets/300.test.txt");
    }
}
