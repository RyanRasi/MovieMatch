package org.moviematch.recommender;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.spark.ml.recommendation.ALSModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;

public class HybridRecommender {
    public static void main(String[] args) throws IOException, TasteException {
        /*
        // Create SparkSession
        SparkSession spark = SparkSession.builder()
                .appName("HybridModelRecommendations")
                .config("spark.master", "local")
                .getOrCreate();

        // Load the saved collaborative filtering model
        ALSModel collaborativeModel = ALSModel.load("spark_collaborative_model");

        // Load the saved Mahout content-based model
        File modelFile = new File("mahout_model.bin");
        DataModel dataModel = new FileDataModel(modelFile);
        GenericItemBasedRecommender contentBasedModel = new GenericItemBasedRecommender(dataModel, new UncenteredCosineSimilarity(dataModel));

        // Get the user-selected movies
        int[] selectedMovieIds = {110, 147, 858};

        // Generate collaborative filtering recommendations
        Dataset<Row> collaborativeRecommendations = collaborativeModel
                .recommendForAllItems(5)
                .filter(functions.array_contains(functions.array(
                        null, Arrays.stream(selectedMovieIds)
                                .boxed()
                                .map(String::valueOf)
                                .toArray(String[]::new)
                ), functions.col("movieId")))
                .select("movieId", "recommendations.userId")
                .selectExpr("userId", "explode(recommendations.movieId) as rec");

        // Generate content-based recommendations using Mahout
        List<RecommendedItem> contentBasedRecommendations = new ArrayList<>();
        for (int movieId : selectedMovieIds) {
            contentBasedRecommendations.addAll(contentBasedModel.recommend(movieId, 5));
        }

        // Convert content-based recommendations to Spark DataFrame
        List<Row> contentBasedRows = contentBasedRecommendations.stream()
                .map(recommendedItem -> RowFactory.create(recommendedItem.getItemID()))
                .collect(Collectors.toList());
        Dataset<Row> contentBasedDF = spark.createDataFrame(contentBasedRows, DataTypes.createStructType(
                new StructField[]{DataTypes.createStructField("movieId", DataTypes.IntegerType, false)})
        );

        // Combine the recommendations from both models
        Dataset<Row> hybridRecommendations = collaborativeRecommendations
                .union(contentBasedDF)
                .groupBy("userId")
                .agg(functions.collect_list("rec").alias("recommendations"));

        // Show the hybrid recommendations
        hybridRecommendations.show(false);

        // Stop SparkSession
        spark.stop();
        */
    }
}
