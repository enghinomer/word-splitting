package com.ionos.domains.demo.configuration;

import com.ionos.domains.demo.model.ProbDistribution;
import com.ionos.domains.demo.service.ProbabilityService;
import com.ionos.domains.demo.service.WordsEmbeddingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import redis.clients.jedis.Jedis;

import java.io.IOException;

@Configuration
public class ApplicationConfiguration {

    public static final String EN_UNIGRAM = "EN-unigram";
    public static final String EN_BIGRAM = "EN-bigram";
    public static final String EN_WORD_EMBD = "EN-WordEmbd";

    @Autowired
    public Jedis jedis;

    @Bean
    public ProbabilityService enUnigramsDistribution() throws IOException {
        return new ProbabilityService("count_1w.txt", EN_UNIGRAM, jedis);
    }

    @Bean
    public ProbabilityService enBigramsDistribution() throws IOException {
        return new ProbabilityService("count_2w.txt", EN_BIGRAM, jedis);
    }

    @Bean
    public WordsEmbeddingsService enWordsEmbeddings() throws Exception {
        return new WordsEmbeddingsService("/home/enghin/DataSets/slim.cc.en.300.vec",
                EN_WORD_EMBD, jedis);
    }
}
