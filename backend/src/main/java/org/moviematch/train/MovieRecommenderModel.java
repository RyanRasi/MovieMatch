package org.moviematch.train;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MovieRecommenderModel implements Serializable {
    private List<Map<String, Double>> tfidfVectors;
    private Map<String, Integer> genreMap;
    private List<List<String>> genreTokensList;
    private Set<String> uniqueTerms;

    // Constructor to initialize the model
    public MovieRecommenderModel(List<Map<String, Double>> tfidfVectors, Map<String, Integer> genreMap, List<List<String>> genreTokensList, Set<String> uniqueTerms) {
        this.tfidfVectors = tfidfVectors;
        this.genreMap = genreMap;
        this.genreTokensList = genreTokensList;
        this.uniqueTerms = uniqueTerms;
    }

    // Getter methods for the data structures
    public List<Map<String, Double>> getTfidfVectors() {
        return tfidfVectors;
    }

    public Map<String, Integer> getGenreMap() {
        return genreMap;
    }

    public List<List<String>> getGenreTokensList() {
        return genreTokensList;
    }

    public Set<String> getUniqueTerms() {
        return uniqueTerms;
    }
}
