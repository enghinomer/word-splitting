package com.ionos.domains.demo.service;

import com.ionos.domains.demo.model.Candidate;
import com.ionos.domains.demo.model.Domain;
import com.ionos.domains.demo.model.Language;
import com.ionos.domains.demo.service.segmentation.ProbabilityService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@Service
public class SimilarityService {

    @Autowired
    private LanguageDetectionService languageDetectionService;

    @Autowired
    private WordsEmbeddingsService enWordsEmbeddings;

    @Autowired
    private WordsEmbeddingsService esWordsEmbeddings;

    @Autowired
    private WordsEmbeddingsService frWordsEmbeddings;

    @Autowired
    private WordsEmbeddingsService itWordsEmbeddings;

    @Autowired
    private WordsEmbeddingsService deWordsEmbeddings;

    @Autowired
    private JedisPool pool;

    private Jedis jedis;

    private static final String TLDS_SIM_FILE = "datasets/slimTlds.txt";
    private static final String TLDS_SIM_KEY = "TLD_SIM";

    @PostConstruct
    private void postConstruct() throws IOException {
        jedis = pool.getResource();
        try (BufferedReader reader = new BufferedReader(new FileReader(TLDS_SIM_FILE))) {
            String line = reader.readLine();
            while (line != null) {
                final String[] split = line.split("\\t");
                jedis.hsetnx(TLDS_SIM_KEY, split[0], split[1]);
                line = reader.readLine();
            }
        }
        jedis.close();
    }


    public double getLevenshteinSimilarity(String word1, String word2) {
        return 1 - (double) this.getLevenshteinDistance(word1, word2)/(Math.max(word1.length(), word2.length()));
    }

    private int getLevenshteinDistance(String word1, String word2) {
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
        return levenshteinDistance.apply(word1, word2);
    }

    public double getCandidatesEmbeddingsSimilarity(Candidate domain1, Candidate domain2) {
        if (!domain1.getLanguage().equals(domain2.getLanguage())) {
            return -10;
        }

        WordsEmbeddingsService embeddingsService = getEmbeddingService(domain1.getLanguage());
        double wordSimilarity = 0.0;
        for (String w1 : domain1.getWords()) {
            for (String w2 : domain2.getWords()) {
                wordSimilarity += embeddingsService.getCosSimilarity(w1, w2);
            }
        }
        return wordSimilarity /(domain1.getWords().size() + domain2.getWords().size());
    }

    public double getDomainsEmbeddingsSimilarity(Domain domain1, Domain domain2) {
        if (!domain1.getLanguage().equals(domain2.getLanguage())) {
            return -10;
        }

        WordsEmbeddingsService embeddingsService = getEmbeddingService(domain1.getLanguage());
        double wordSimilarity = 0.0;
        for (String w1 : domain1.getKeyWords()) {
            for (String w2 : domain2.getKeyWords()) {
                wordSimilarity += embeddingsService.getCosSimilarity(w1, w2);
            }
        }
        return wordSimilarity /(domain1.getKeyWords().size() + domain2.getKeyWords().size());
    }

    public double getEmbeddingsSimilarity(Domain domain1, Domain domain2) {
        if (!domain1.getLanguage().equals(domain2.getLanguage())) {
            return -10;
        }

        WordsEmbeddingsService embeddingsService = getEmbeddingService(domain1.getLanguage());
        double wordSimilarity = 0.0;
        for (double[] e1 : domain1.getWordEmbeddings()) {
            for (double[] e2 : domain2.getWordEmbeddings()) {
                wordSimilarity += embeddingsService.getCosineSimilarity(e1, e2);
            }
        }
        return wordSimilarity /(domain1.getKeyWords().size() + domain2.getKeyWords().size());
    }

    public double getSIFsimilarity(Domain domain1, Domain domain2) {
        if (!domain1.getLanguage().equals(domain2.getLanguage())) {
            return -10;
        }

        WordsEmbeddingsService embeddingsService = getEmbeddingService(domain1.getLanguage());
        double[] embeddingAvg1 = new double[300];
        double[] embeddingAvg2 = new double[300];

        for (int i=0; i<domain1.getWordEmbeddings().size(); i++) {
            double[] e1 = domain1.getWordEmbeddings().get(i);
            if (e1 != null) {
                for (int j = 0; j < 300; j++) {
                    embeddingAvg1[j] += e1[j] / (domain1.getWordEmbeddings().size());
                }
            }
        }

        for (int i=0; i<domain2.getWordEmbeddings().size(); i++) {
            double[] e2 = domain2.getWordEmbeddings().get(i);
            if (e2 != null) {
                for (int j = 0; j < 300; j++) {
                    embeddingAvg2[j] += e2[j] / (domain2.getWordEmbeddings().size());
                }
            }
        }

        return embeddingsService.getCosineSimilarity(embeddingAvg1, embeddingAvg2);
    }

    public double getTldsSimilarity(String tld1, String tld2) {
        Jedis jedisInstance = pool.getResource();
        /*if (Boolean.FALSE.equals(jedisInstance.hexists(TLDS_SIM_KEY, tld1))) {
            jedisInstance.close();
            return 0.0;
        }*/
        String tldSimilarities = jedisInstance.hget(TLDS_SIM_KEY, tld1);
        jedisInstance.close();
        if (tldSimilarities == null) {
            return 0.0;
        }
        final int beginIndex = tldSimilarities.indexOf(tld2 + ":");
        if (beginIndex == -1) {
            return 0.0;
        }
        String firstSubString = tldSimilarities.substring(beginIndex);
        String sim;
        int firstSpaceIndex = firstSubString.indexOf(" ");
        if (firstSpaceIndex != -1) {
            sim = firstSubString.substring(firstSubString.indexOf(":") + 1, firstSpaceIndex);
        } else {
            sim = firstSubString.substring(firstSubString.indexOf(":") + 1);
        }
        if (StringUtils.isBlank(sim)) {
            return 0.0;
        }
        return Double.parseDouble(sim);
    }

    private WordsEmbeddingsService getEmbeddingService(Language language) {
        switch (language) {
            case EN:
                return enWordsEmbeddings;
            case DE:
                return deWordsEmbeddings;
            case ES:
                return esWordsEmbeddings;
            case FR:
                return frWordsEmbeddings;
            case IT:
                return itWordsEmbeddings;
            default:
                throw new IllegalArgumentException();
        }
    }

}
