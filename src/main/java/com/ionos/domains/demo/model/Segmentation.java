package com.ionos.domains.demo.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Segmentation implements Comparable<Segmentation> {

    private double probability;
    private List<String> words;
    private Language language;

    public Segmentation() {
    }

    public Segmentation(double probability, List<String> words) {
        this.probability = probability;
        this.words = new ArrayList<>(words);
    }

    public Segmentation(double probability, List<String> words, Language language) {
        this.probability = probability;
        this.words = words;
        this.language = language;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Segmentation segmentation = (Segmentation) o;
        return Double.compare(segmentation.probability, probability) == 0 &&
                Objects.equals(words, segmentation.words) &&
                language == segmentation.language;
    }

    @Override
    public int hashCode() {
        return Objects.hash(probability, words, language);
    }

    @Override
    public int compareTo(Segmentation o) {
        return Double.compare(o.getProbability(), this.getProbability());
    }
}
