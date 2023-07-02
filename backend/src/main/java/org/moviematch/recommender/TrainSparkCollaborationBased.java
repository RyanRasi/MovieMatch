package org.moviematch.recommender;

import java.io.IOException;

import org.apache.spark.ml.recommendation.ALS;
import org.apache.spark.ml.recommendation.ALSModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class TrainSparkCollaborationBased {

    public static final String MOVIE_RATINGS_PATH = "./src/main/resources/data/100k/ratings.csv";
    public static final String MODEL_PATH = "./src/main/resources/models/collaborative_model";

    public static void main(String[] args) {
        // Create SparkSession
        SparkSession spark = SparkSession.builder()
                .appName("SparkCollaborativeModelTrainer")
                .config("spark.master", "local")
                .getOrCreate();

        // Load ratings data (userId, movieId, rating)
        Dataset<Row> ratingsDF = spark.read()
                .option("header", true)
                .csv(MOVIE_RATINGS_PATH)
                .na().drop()
                .selectExpr("CAST(userId AS INT)", "CAST(movieId AS INT)", "CAST(rating AS FLOAT)");

        // Train the collaborative filtering model
        ALS als = new ALS()
                .setMaxIter(10)
                .setRegParam(0.01)
                .setUserCol("userId")
                .setItemCol("movieId")
                .setRatingCol("rating");

        ALSModel collaborativeModel = als.fit(ratingsDF);

        // Save the collaborative filtering model
        try {
			collaborativeModel.save(MODEL_PATH);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        System.out.println("Spark collaborative filtering model trained and saved successfully!");

        // Stop SparkSession
        spark.stop();
    }
}
