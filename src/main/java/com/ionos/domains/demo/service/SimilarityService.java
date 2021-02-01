package com.ionos.domains.demo.service;

import com.ionos.domains.demo.model.Candidate;
import com.ionos.domains.demo.model.Language;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimilarityService {

    @Autowired
    private LanguageDetectionService languageDetectionService;

    @Autowired
    private WordsEmbeddingsService enWordsEmbeddings;



    public double getLevenshteinSimilarity(String word1, String word2) {
        return 1 - (double) this.getLevenshteinDistance(word1, word2)/(Math.max(word1.length(), word2.length()));
    }

    private int getLevenshteinDistance(String word1, String word2) {
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
        return levenshteinDistance.apply(word1, word2);
    }

    public double getDomainsEmbeddingsSimilarity(Candidate domain1, Candidate domain2) {
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

    private WordsEmbeddingsService getEmbeddingService(Language language) {
        switch (language) {
            case EN:
                return enWordsEmbeddings;
            case DE:
            case ES:
            case FR:
            case IT:
            default:
                throw new IllegalArgumentException();
        }
    }

}
