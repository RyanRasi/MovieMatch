package org.moviematch;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.*;

class MovieRecommenderModel implements Serializable {
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

public class TrainMahoutContentBased {

    public static final String MOVIE_CONTENT_PATH = "./src/main/resources/data/100k/movies.csv";
    public static final String MODEL_PATH = "./src/main/resources/models/content_model.ser";

    public static void main(String[] args) {
        Map<String, Integer> genreMap = new HashMap<>();
        List<List<String>> genreTokensList = new ArrayList<>();
        Set<String> uniqueTerms = new HashSet<>();
        List<Map<String, Integer>> termDocumentMatrix = new ArrayList<>();
        List<Map<String, Double>> tfVectors = new ArrayList<>();
        Map<String, Double> idfVector = new HashMap<>();
        List<Map<String, Double>> tfidfVectors = new ArrayList<>();

        try (FileReader reader = new FileReader(MOVIE_CONTENT_PATH);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader())) {

            // Split genres and collect unique terms
            for (CSVRecord record : csvParser) {
                String genres = record.get(2); // Read the third column for the genres
                List<String> tokens = Arrays.asList(genres.split("\\|"));
                genreTokensList.add(tokens);
                uniqueTerms.addAll(tokens);
            }

            // Create term-document matrix
            int numDocs = genreTokensList.size();
            int numTerms = uniqueTerms.size();
            for (List<String> tokens : genreTokensList) {
                Map<String, Integer> termCounts = new HashMap<>();
                for (String term : tokens) {
                    termCounts.put(term, termCounts.getOrDefault(term, 0) + 1);
                }
                termDocumentMatrix.add(termCounts);
            }

            // Calculate TF (Term Frequency) vectors
            for (Map<String, Integer> termCounts : termDocumentMatrix) {
                Map<String, Double> tfVector = new HashMap<>();
                for (Map.Entry<String, Integer> entry : termCounts.entrySet()) {
                    String term = entry.getKey();
                    int termCount = entry.getValue();
                    double tf = (double) termCount / termCounts.size();
                    tfVector.put(term, tf);
                }
                tfVectors.add(tfVector);
            }

            // Calculate IDF (Inverse Document Frequency) vector
            for (String term : uniqueTerms) {
                int docsWithTerm = 0;
                for (Map<String, Integer> termCounts : termDocumentMatrix) {
                    if (termCounts.containsKey(term)) {
                        docsWithTerm++;
                    }
                }
                double idf = Math.log((double) numDocs / (1 + docsWithTerm));
                idfVector.put(term, idf);
            }

            // Calculate TF-IDF vectors
            for (Map<String, Double> tfVector : tfVectors) {
                Map<String, Double> tfidfVector = new HashMap<>();
                for (Map.Entry<String, Double> entry : tfVector.entrySet()) {
                    String term = entry.getKey();
                    double tf = entry.getValue();
                    double idf = idfVector.getOrDefault(term, 0.0);
                    double tfidf = tf * idf;
                    tfidfVector.put(term, tfidf);
                }
                tfidfVectors.add(tfidfVector);
            }

            // Create the MovieRecommenderModel instance
            MovieRecommenderModel movieRecommenderModel = new MovieRecommenderModel(tfidfVectors, genreMap, genreTokensList, uniqueTerms);

            // Save the model to a file using serialization
            try (FileOutputStream fileOut = new FileOutputStream(MODEL_PATH);
                 ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
                objectOut.writeObject(movieRecommenderModel);
                System.out.println("Movie recommender model saved successfully!");
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
