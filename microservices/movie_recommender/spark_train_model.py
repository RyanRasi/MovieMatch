from pyspark.sql import SparkSession
from pyspark.ml.recommendation import ALS
from pyspark.ml.evaluation import RegressionEvaluator
from pyspark.sql.functions import col

def train():
    # Create a Spark session
    spark = SparkSession.builder.appName("MovieRecommendation").getOrCreate()

    # Load ratings data
    ratings = spark.read.csv("./data/moviesdataset/ratings_small.csv", header=True, inferSchema=True)

    # Split the data into training and testing sets
    (train, test) = ratings.randomSplit([0.7, 0.3], seed=42)

    # Build the ALS model
    als = ALS(
        userCol="userId",
        itemCol="movieId",
        ratingCol="rating",
        coldStartStrategy="drop",
        rank=10,
        maxIter=10,
        regParam=0.2
    )

    model = als.fit(train)

    # Save the ALS model
    model.save("../models/als_model")

    # Evaluate the model on the test set
    predictions = model.transform(test)

    evaluator = RegressionEvaluator(
        metricName="rmse",
        labelCol="rating",
        predictionCol="prediction"
    )

    rmse = evaluator.evaluate(predictions)
    print(f"Root Mean Squared Error (RMSE) = {rmse}")

    # Define a threshold for considering a recommendation as relevant
    threshold = 3.5

    # Calculate precision and recall
    relevant_predictions = predictions.filter(col("prediction") >= threshold)
    relevant_test_set = test.filter(col("rating") >= threshold)

    precision = relevant_predictions.count() / predictions.count()
    recall = relevant_predictions.count() / relevant_test_set.count()

    print(f"Precision: {precision}")
    print(f"Recall: {recall}")

    # Generate top N movie recommendations for each user
    userRecs = model.recommendForAllUsers(5)

    # Show the top N recommendations for a specific user (e.g., user with id=1)
    user_id = 565
    user_recommendations = userRecs.filter(userRecs.userId == user_id).select("recommendations.movieId", "recommendations.rating")

    # Show the top N recommendations for the user
    user_recommendations.show(truncate=False)

    # Evaluate precision and recall for the specific user
    user_predictions = model.transform(test.filter(col("userId") == user_id))
    user_relevant_predictions = user_predictions.filter(col("prediction") >= threshold)

    user_precision = user_relevant_predictions.count() / user_predictions.count()
    user_recall = user_relevant_predictions.count() / relevant_test_set.filter(col("userId") == user_id).count()

    print(f"User Precision: {user_precision}")
    print(f"User Recall: {user_recall}")