package org.moviematch.controller;

import org.moviematch.recommend.*;
import org.moviematch.train.*;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MovieController {

    @RequestMapping("/recommendCollab")
    public String getCollab(@RequestParam(name = "movieIds") List<Integer> movieIds) {
        // Create an instance of CollaborativeRecommender
        CollaborativeRecommender recommender = new CollaborativeRecommender();
        // Call the generateRecommendations method
        String results = recommender.generateRecommendations(movieIds);
        return results;
    }
    @RequestMapping("/recommendContent")
    public String getContent(@RequestParam(name = "movieIds") List<Integer> movieIds) {
        // Create an instance of CollaborativeRecommender
        ContentRecommender recommender = new ContentRecommender();
        // Call the generateRecommendations method
        String results = recommender.generateRecommendations(movieIds);
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
    public String getRecommendations(@RequestParam(name = "movieIds") List<Integer> movieIds) {
        // Create an instance of HybridRecommender
        HybridRecommender recommender = new HybridRecommender();
        // Call the generateRecommendations method
        String results = recommender.generateHybridRecommendations(movieIds).toString();
        return results;
    }
}
