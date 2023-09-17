package org.moviematch.recommend;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HybridRecommender {

    private final ContentRecommender contentRecommender;
    private final CollaborativeRecommender collaborativeRecommender;

    // Define weights for content-based and collaborative-based recommendations
    private double contentWeight = 0.7; // Adjust this value based on importance
    private double collaborativeWeight = 0.3; // Adjust this value based on importance

    public HybridRecommender() {
        this.contentRecommender = new ContentRecommender();
        this.collaborativeRecommender = new CollaborativeRecommender();
    }

    public List<Integer> generateHybridRecommendations() {
        // Generate content-based recommendations
        String contentRecommendationsStr = contentRecommender.generateRecommendations();

        // Generate collaborative-based recommendations
        String collaborativeRecommendationsStr = collaborativeRecommender.generateRecommendations();

        // Convert String object to Integer List
        List<Integer> contentRecommendationsInt = extractContentMovieIdsFromJson("[" + contentRecommendationsStr + "]");
        List<Integer> collabRecommendationsInt = extractCollabMovieIdsFromJson("[" + collaborativeRecommendationsStr + "]");
        // Combine the recommendations using weighted averaging
        List<Integer> hybridRecommendations = new ArrayList<>();

        // Calculate the number of recommendations based on the weights
        int numContentRecommendations = (int) (contentRecommendationsInt.size() * contentWeight);
        int numCollaborativeRecommendations = (int) (collabRecommendationsInt.size() * collaborativeWeight);

        // Add content-based recommendations with the adjusted weight
        hybridRecommendations.addAll(contentRecommendationsInt.subList(0, numContentRecommendations));

        // Add collaborative-based recommendations with the adjusted weight
        hybridRecommendations.addAll(collabRecommendationsInt.subList(0, numCollaborativeRecommendations));

        return hybridRecommendations;
    }

    public static List<Integer> extractContentMovieIdsFromJson(String inputText) {
            List<Integer> movieIds = new ArrayList<>();
            Pattern pattern = Pattern.compile("Movie ID: (\\d+)");
            Matcher matcher = pattern.matcher(inputText);

            while (matcher.find()) {
                int movieId = Integer.parseInt(matcher.group(1));
                movieIds.add(movieId);
            }

            return movieIds;
    }

    public static List<Integer> extractCollabMovieIdsFromJson(String inputJson) {

        List<Integer> movieIds = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\[(\\d+),"); // Matches the integer within []
        Matcher matcher = pattern.matcher(inputJson);

        while (matcher.find()) {
            int movieId = Integer.parseInt(matcher.group(1));
            movieIds.add(movieId);
        }
        movieIds.remove(0);

        return movieIds;
    }

    public static void main(String[] args) {
        HybridRecommender hybridRecommender = new HybridRecommender();
        List<Integer> recommendations = hybridRecommender.generateHybridRecommendations();

        System.out.println("Hybrid Recommendations:");
        for (int movieID : recommendations) {
            System.out.println("Movie ID: " + movieID);
        }
    }
}
