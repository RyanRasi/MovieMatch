package org.moviematch.recommend;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.moviematch.train.MovieRecommenderModel;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;

public class ContentRecommender {

    public static final String MODEL_PATH = "./src/main/resources/models/content_model.ser";

    public String generateRecommendations(List<Integer> movieIds) {
        // Load the movie recommender model from the saved file
        MovieRecommenderModel movieRecommenderModel = loadModel(MODEL_PATH);

        if (movieRecommenderModel != null) {
            List<Map<String, Double>> tfidfVectors = movieRecommenderModel.getTfidfVectors();
            Map<String, Integer> genreMap = movieRecommenderModel.getGenreMap();
            List<List<String>> genreTokensList = movieRecommenderModel.getGenreTokensList();
            Set<String> uniqueTerms = movieRecommenderModel.getUniqueTerms();

            // Get recommendations for three target movies
            //int[] targetMovieIDs = {6712, 7770}; // Specify the target movie IDs for recommendations
            //int[] targetMovieIDs = {59315, 77561, 102125};
            int[] targetMovieIDs = movieIds.stream()
                    .mapToInt(Integer::intValue)
                    .toArray();

            int numRecommendations = 5; // Number of recommendations to retrieve

            List<MovieSimilarity> movieSimilarities = new ArrayList<>();

            for (int movieID : targetMovieIDs) {
                // Calculate average TF-IDF vector for the target movie
                Map<String, Double> averageTFIDFVector = calculateAverageTFIDFVector(movieID, tfidfVectors, targetMovieIDs.length);

                // Calculate cosine similarity with other movies
                for (int i = 0; i < tfidfVectors.size(); i++) {
                    if (!Arrays.asList(targetMovieIDs).contains(i + 1)) {
                        Map<String, Double> currentMovieTFIDFVector = tfidfVectors.get(i);
                        double cosineSimilarity = calculateCosineSimilarity(averageTFIDFVector, currentMovieTFIDFVector);
                        movieSimilarities.add(new MovieSimilarity(i + 1, cosineSimilarity));
                    }
                }
            }

            // Sort movies by cosine similarity in descending order
            Collections.sort(movieSimilarities, Comparator.reverseOrder());

            // Get top recommendations
            List<MovieSimilarity> topRecommendations = movieSimilarities.subList(0, numRecommendations);

            //
            String csvFilePath = "./src/main/resources/data/100k/movies.csv";
            String columnName = "title"; // Name of the column

            StringBuilder output = new StringBuilder();

            // Print recommendations
            System.out.println("Top " + numRecommendations + " Recommendations based on the three target movies:");
            for (MovieSimilarity recommendation : topRecommendations) {
                System.out.println("Movie ID: " + recommendation.getMovieID() + ", Similarity: " + recommendation.getSimilarity());
                output.append("{Movie ID: ").append(recommendation.getMovieID())
                        .append(", Similarity: ").append(recommendation.getSimilarity()).append("\n");
                int rowNumber = recommendation.getMovieID(); // Row number (1-based index)
                // Get the CSV record at the specified row number
                try (FileReader reader = new FileReader(csvFilePath);
                     CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader())) {
                CSVRecord record = csvParser.getRecords().get(rowNumber - 1);

                // Get the value in the specified column
                String columnValue = record.get(columnName);
                output.append(columnValue).append("}, \n");
                // Print the value
                System.out.println(columnValue + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return output.toString();
        }
        return null;
    }

    private static MovieRecommenderModel loadModel(String filePath) {
        try (FileInputStream fileIn = new FileInputStream(filePath);
             ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {
            return (MovieRecommenderModel) objectIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Map<String, Double> calculateAverageTFIDFVector(int movieID, List<Map<String, Double>> tfidfVectors, int numTargetMovies) {
        Map<String, Double> averageTFIDFVector = new HashMap<>();

        for (Map<String, Double> tfidfVector : tfidfVectors) {
            if (tfidfVectors.indexOf(tfidfVector) == (movieID - 1)) {
                for (Map.Entry<String, Double> entry : tfidfVector.entrySet()) {
                    String term = entry.getKey();
                    double tfidf = entry.getValue();
                    averageTFIDFVector.put(term, averageTFIDFVector.getOrDefault(term, 0.0) + tfidf);
                }
            }
        }

        // Calculate average by dividing each term's TF-IDF value by the number of target movies
        for (Map.Entry<String, Double> entry : averageTFIDFVector.entrySet()) {
            entry.setValue(entry.getValue() / numTargetMovies);
        }

        return averageTFIDFVector;
    }

    private static double calculateCosineSimilarity(Map<String, Double> vector1, Map<String, Double> vector2) {
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (Map.Entry<String, Double> entry : vector1.entrySet()) {
            String term = entry.getKey();
            double value1 = entry.getValue();
            double value2 = vector2.getOrDefault(term, 0.0);
            dotProduct += value1 * value2;
            norm1 += value1 * value1;
        }

        for (double value : vector2.values()) {
            norm2 += value * value;
        }

        double normProduct = Math.sqrt(norm1) * Math.sqrt(norm2);

        if (normProduct == 0.0) {
            return 0.0;
        } else {
            return dotProduct / normProduct;
        }
    }

    private static class MovieSimilarity implements Comparable<MovieSimilarity> {
        private int movieID;
        private double similarity;

        public MovieSimilarity(int movieID, double similarity) {
            this.movieID = movieID;
            this.similarity = similarity;
        }

        public int getMovieID() {
            // Minus 1 due to the header file
            return movieID;
        }

        public double getSimilarity() {
            return similarity;
        }

        @Override
        public int compareTo(MovieSimilarity other) {
            return Double.compare(this.similarity, other.similarity);
        }
    }
}
