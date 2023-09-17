package org.moviematch.controller;

import org.moviematch.recommend.*;
import org.moviematch.train.*;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MovieController {

    @RequestMapping("/recommendCollab")
    public String getCollab() {
        // Create an instance of CollaborativeRecommender
        CollaborativeRecommender recommender = new CollaborativeRecommender();
        // Call the generateRecommendations method
        String results = recommender.generateRecommendations();
        return results;
    }
    @RequestMapping("/recommendContent")
    public String getContent() {
        // Create an instance of CollaborativeRecommender
        ContentRecommender recommender = new ContentRecommender();
        // Call the generateRecommendations method
        String results = recommender.generateRecommendations();
        return results;
    }
    @RequestMapping("/trainContent")
    public String trainContent() {
        // Create an instance of CollaborativeRecommender
        TrainMahoutContentBased trainer = new TrainMahoutContentBased();
        // Call the generateRecommendations method
        String results = trainer.trainRecommendations();
        return results;
    }
    @RequestMapping("/recommendHybrid")
    public String getRecommendations() {
        // Create an instance of HybridRecommender
        HybridRecommender recommender = new HybridRecommender();
        // Call the generateRecommendations method
        String results = recommender.generateHybridRecommendations().toString();
        return results;
    }
}
