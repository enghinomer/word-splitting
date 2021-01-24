package com.ionos.domains.demo.configuration;

import com.ionos.domains.demo.model.ProbDistribution;
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
}
