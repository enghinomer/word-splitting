package com.ionos.domains.demo.configuration;

import com.ionos.domains.demo.service.segmentation.ProbabilityService;
import com.ionos.domains.demo.service.WordsEmbeddingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    public static final String ES_WORD_EMBD = "ES-WordEmbd";
    public static final String FR_WORD_EMBD = "FR-WordEmbd";
    public static final String IT_WORD_EMBD = "IT-WordEmbd";
    public static final String DE_WORD_EMBD = "DE-WordEmbd";

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
        return new ProbabilityService("datasets/new/en-unigrams.txt", EN_UNIGRAM, jedis, EN_TOKENS);
    }

    @Bean
    public ProbabilityService enBigramsDistribution() throws IOException {
        return new ProbabilityService("datasets/new/en-bigrams.txt", EN_BIGRAM, jedis, EN_TOKENS);
    }

    @Bean
    public ProbabilityService deUnigramsDistribution() throws IOException {
        return new ProbabilityService("datasets/new/de-unigrams.txt", DE_UNIGRAM, jedis, DE_TOKENS);
    }

    @Bean
    public ProbabilityService deBigramsDistribution() throws IOException {
        return new ProbabilityService("datasets/new/de-bigrams.txt", DE_BIGRAM, jedis, DE_TOKENS);
    }

    @Bean
    public ProbabilityService esUnigramsDistribution() throws IOException {
        return new ProbabilityService("datasets/new/es-unigrams.txt", ES_UNIGRAM, jedis, ES_TOKENS);
    }

    @Bean
    public ProbabilityService esBigramsDistribution() throws IOException {
        return new ProbabilityService("datasets/new/es-bigrams.txt", ES_BIGRAM, jedis, ES_TOKENS);
    }

    @Bean
    public ProbabilityService frUnigramsDistribution() throws IOException {
        return new ProbabilityService("datasets/new/fr-unigrams.txt", FR_UNIGRAM, jedis, FR_TOKENS);
    }

    @Bean
    public ProbabilityService frBigramsDistribution() throws IOException {
        return new ProbabilityService("datasets/new/fr-bigrams.txt", FR_BIGRAM, jedis, FR_TOKENS);
    }

    @Bean
    public ProbabilityService itUnigramsDistribution() throws IOException {
        return new ProbabilityService("datasets/new/it-unigrams.txt", IT_UNIGRAM, jedis, IT_TOKENS);
    }

    @Bean
    public ProbabilityService itBigramsDistribution() throws IOException {
        return new ProbabilityService("datasets/new/it-bigrams.txt", IT_BIGRAM, jedis, IT_TOKENS);
    }

    @Bean
    public WordsEmbeddingsService enWordsEmbeddings() throws Exception {
        return new WordsEmbeddingsService("/home/enghin/Documents/Personal/Projects/wordSuggestion/datasets/wordEmbeddings/slim.cc.en.300.vec",
                EN_WORD_EMBD, jedis);
    }

    @Bean
    public WordsEmbeddingsService esWordsEmbeddings() throws Exception {
        return new WordsEmbeddingsService("/home/enghin/Documents/Personal/Projects/wordSuggestion/datasets/wordEmbeddings/slim.cc.es.300.vec",
                ES_WORD_EMBD, jedis);
    }

    @Bean
    public WordsEmbeddingsService frWordsEmbeddings() throws Exception {
        return new WordsEmbeddingsService("/home/enghin/Documents/Personal/Projects/wordSuggestion/datasets/wordEmbeddings/slim.cc.fr.300.vec",
                FR_WORD_EMBD, jedis);
    }

    @Bean
    public WordsEmbeddingsService itWordsEmbeddings() throws Exception {
        return new WordsEmbeddingsService("/home/enghin/Documents/Personal/Projects/wordSuggestion/datasets/wordEmbeddings/slim.cc.it.300.vec",
                IT_WORD_EMBD, jedis);
    }

    @Bean
    public WordsEmbeddingsService deWordsEmbeddings() throws Exception {
        return new WordsEmbeddingsService("/home/enghin/Documents/Personal/Projects/wordSuggestion/datasets/wordEmbeddings/slim.cc.de.300.vec",
                DE_WORD_EMBD, jedis);
    }
}
