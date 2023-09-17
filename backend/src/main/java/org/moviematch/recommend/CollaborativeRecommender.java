package org.moviematch.recommend;

import org.apache.spark.ml.recommendation.ALS;
import org.apache.spark.ml.recommendation.ALSModel;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.Arrays;
import java.util.List;

public class CollaborativeRecommender {
    public static final String RATINGS_DATA_PATH = "./src/main/resources/data/100k/ratings.csv";
    public String generateRecommendations() {
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

        // Create a new user dataset with a user ID
        int newUserId = 999; // Choose a unique user ID for the new user
        List<Row> newUserData = Arrays.asList(
                RowFactory.create(newUserId, 170957, 5.0F),
                RowFactory.create(newUserId, 45517, 5.0F),
                RowFactory.create(newUserId, 87876, 5.0F),
                RowFactory.create(newUserId, 1, 5.0F),
                RowFactory.create(newUserId, 3114, 5.0F)
        );

        List<StructField> fields = Arrays.asList(
                DataTypes.createStructField("userId", DataTypes.IntegerType, false),
                DataTypes.createStructField("movieId", DataTypes.IntegerType, false),
                DataTypes.createStructField("rating", DataTypes.FloatType, false)
        );

        StructType schema = DataTypes.createStructType(fields);
        Dataset<Row> newUserDataset = spark.createDataFrame(newUserData, schema);

        // Combine the new user dataset with the existing ratings dataset
        Dataset<Row> combinedRatingsDF = ratingsDF.union(newUserDataset);

        // Train the collaborative filtering model
        ALS als = new ALS()
                .setMaxIter(10)
                .setRegParam(0.01)
                .setUserCol("userId")
                .setItemCol("movieId")
                .setRatingCol("rating");

        ALSModel collaborativeModel = als.fit(combinedRatingsDF);

        collaborativeModel.setColdStartStrategy("drop");

        // Get recommendations for the new user
        Dataset<Row> userRecs = collaborativeModel.recommendForUserSubset(newUserDataset, 10);

        Row[] topRows = (Row[]) userRecs.take(10);

        // Convert the rows to a string representation
        StringBuilder output = new StringBuilder();
        for (Row row : topRows) {
            output.append(row.toString()).append("\n");
        }

        // userRecs.show(10, false);

        // Stop SparkSession
        spark.stop();

        return output.toString();
    }
}
