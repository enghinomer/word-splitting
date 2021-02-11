package com.ionos.domains.demo.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProbDistribution {

    private long N = 1708180445L; // nr of tokens in corpus
    private Map<String, Long> data = new HashMap<>();
    private BufferedReader reader;
    private long numberOfTokens;

    public ProbDistribution(String fileName, long N) throws IOException {
        this.N = N;
        reader = new BufferedReader(new FileReader(fileName));
        String line = reader.readLine();
        while (line != null) {
            final String[] split = line.split("\\t");
            long frequency = Long.parseLong(split[1]);
            if (!data.containsKey(split[0].toLowerCase())) {
                data.put(split[0].toLowerCase(), frequency);
            }
            this.numberOfTokens += frequency;
            line = reader.readLine();
        }
    }

    public double getGramProbability(String gram) {
        if (data.containsKey(gram)) {
            return (double)data.get(gram)/N;
        }
        return getProbabilityUnkGram(gram);
    }

    public Map<String, Long> getData() {
        return data;
    }

    private double getProbabilityUnkGram(String gram) {
        // avoid long words
        long temp = 4178523593L;
        return 10/(temp * (Math.pow(10, gram.length())));
    }

    public static void main(String[] args) throws IOException {
        //ProbDistribution probDistribution = new ProbDistribution("count_2w.txt");
        //System.out.println(probDistribution.numberOfTokens);
    }
}
