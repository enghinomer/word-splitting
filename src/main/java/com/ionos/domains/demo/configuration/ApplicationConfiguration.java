package com.ionos.domains.demo.configuration;

import com.ionos.domains.demo.model.ProbDistribution;
import com.ionos.domains.demo.service.ProbabilityService;
import com.ionos.domains.demo.service.WordsEmbeddingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ApplicationConfiguration {

    public static final String EN_UNIGRAM = "EN-unigram";
    public static final String EN_BIGRAM = "EN-bigram";
    public static final String DE_UNIGRAM = "DE-unigram";
    public static final String DE_BIGRAM = "DE-bigram";
    public static final String ES_UNIGRAM = "ES-unigram";
    public static final String ES_BIGRAM = "ES-bigram";
    public static final String FR_UNIGRAM = "FR-unigram";
    public static final String FR_BIGRAM = "FR-bigram";
    public static final String IT_UNIGRAM = "IT-unigram";
    public static final String IT_BIGRAM = "IT-bigram";

    public static final String EN_WORD_EMBD = "EN-WordEmbd";

    public static final long EN_TOKENS = 1708180445L;
    public static final long DE_TOKENS = 2562270667L;
    public static final long ES_TOKENS = 1249309182L;
    public static final long FR_TOKENS = 614851502L;
    public static final long IT_TOKENS = 4178523593L;

    @Autowired
    public JedisPool jedis;

    @Bean("fixedThreadPool")
    public ExecutorService cachedThreadPool() {
        return Executors.newFixedThreadPool(32);
    }

    @Bean
    public ProbabilityService enUnigramsDistribution() throws IOException {
        return new ProbabilityService("en_unigrams.txt", EN_UNIGRAM, jedis, EN_TOKENS);
    }

    @Bean
    public ProbabilityService enBigramsDistribution() throws IOException {
        return new ProbabilityService("en_bigrams.txt", EN_BIGRAM, jedis, EN_TOKENS);
    }

    @Bean
    public ProbabilityService deUnigramsDistribution() throws IOException {
        return new ProbabilityService("de_unigrams.txt", DE_UNIGRAM, jedis, DE_TOKENS);
    }

    @Bean
    public ProbabilityService deBigramsDistribution() throws IOException {
        return new ProbabilityService("de_bigrams.txt", DE_BIGRAM, jedis, DE_TOKENS);
    }

    @Bean
    public ProbabilityService esUnigramsDistribution() throws IOException {
        return new ProbabilityService("es_unigrams.txt", ES_UNIGRAM, jedis, ES_TOKENS);
    }

    @Bean
    public ProbabilityService esBigramsDistribution() throws IOException {
        return new ProbabilityService("es_bigrams.txt", ES_BIGRAM, jedis, ES_TOKENS);
    }

    @Bean
    public ProbabilityService frUnigramsDistribution() throws IOException {
        return new ProbabilityService("fr_unigrams.txt", FR_UNIGRAM, jedis, FR_TOKENS);
    }

    @Bean
    public ProbabilityService frBigramsDistribution() throws IOException {
        return new ProbabilityService("fr_bigrams.txt", FR_BIGRAM, jedis, FR_TOKENS);
    }

    @Bean
    public ProbabilityService itUnigramsDistribution() throws IOException {
        return new ProbabilityService("it_unigrams.txt", IT_UNIGRAM, jedis, IT_TOKENS);
    }

    @Bean
    public ProbabilityService itBigramsDistribution() throws IOException {
        return new ProbabilityService("it_bigrams.txt", IT_BIGRAM, jedis, IT_TOKENS);
    }

    @Bean
    public WordsEmbeddingsService enWordsEmbeddings() throws Exception {
        return new WordsEmbeddingsService("/home/enghin/Documents/Personal/Projects/wordSuggestion/datasets/wordEmbeddings/slim.cc.en.300.vec",
                EN_WORD_EMBD, jedis);
    }
}
