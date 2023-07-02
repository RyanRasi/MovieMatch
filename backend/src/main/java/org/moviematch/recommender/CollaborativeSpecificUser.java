package org.moviematch.recommender;

import org.apache.spark.ml.recommendation.ALS;
import org.apache.spark.ml.recommendation.ALSModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;

import java.util.Arrays;
import java.util.List;

public class CollaborativeSpecificUser {

    public static final String RATINGS_DATA_PATH = "./src/main/resources/data/100k/ratings.csv";

    public static void main(String[] args) {
        // Create SparkSession
        SparkSession spark = SparkSession.builder()
                .appName("MovieRecommendationGenerator")
                .config("spark.master", "local")
                .getOrCreate();

        // Load ratings data (userId, movieId, rating)
        Dataset<Row> ratingsDF = spark.read()
                .option("header", true)
                .csv(RATINGS_DATA_PATH)
                .selectExpr("CAST(userId AS INT)", "CAST(movieId AS INT)", "CAST(rating AS FLOAT)");

        // Train the collaborative filtering model
        ALS als = new ALS()
                .setMaxIter(10)
                .setRegParam(0.01)
                .setUserCol("userId")
                .setItemCol("movieId")
                .setRatingCol("rating");

        ALSModel collaborativeModel = als.fit(ratingsDF);

        // Generate recommendations for user 1
        int userId = 1;
        List<Row> userRows = Arrays.asList(RowFactory.create(userId));
        Dataset<Row> userDF = spark.createDataFrame(userRows, DataTypes.createStructType(Arrays.asList(DataTypes.createStructField("userId", DataTypes.IntegerType, false))));

        Dataset<Row> userRecs = collaborativeModel.recommendForUserSubset(userDF, 5);

        // Display the recommended movies
        System.out.println("Recommended movies for User " + userId + ":");
        userRecs.select("recommendations.movieId", "recommendations.rating").show(false);

        // Stop SparkSession
        spark.stop();
    }
}
