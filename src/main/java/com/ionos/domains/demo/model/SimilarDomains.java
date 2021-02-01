package com.ionos.domains.demo.model;

import java.util.List;

public class SimilarDomains {

    private List<String> levenshteinDomains;

    private List<String> embeddingsDomains;

    public List<String> getLevenshteinDomains() {
        return levenshteinDomains;
    }

    public void setLevenshteinDomains(List<String> levenshteinDomains) {
        this.levenshteinDomains = levenshteinDomains;
    }

    public List<String> getEmbeddingsDomains() {
        return embeddingsDomains;
    }

    public void setEmbeddingsDomains(List<String> embeddingsDomains) {
        this.embeddingsDomains = embeddingsDomains;
    }
}
