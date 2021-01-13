import java.util.ArrayList;
import java.util.List;

public class Candidate {

    private double probability;
    private List<String> words;

    public Candidate() {
    }

    public Candidate(double probability, List<String> words) {
        this.probability = probability;
        this.words = new ArrayList<>(words);
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
}
