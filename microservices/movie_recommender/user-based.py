from pyspark.sql import SparkSession
from pyspark.ml.recommendation import ALSModel
from pyspark.sql.functions import col

def recommendUser(user_id):
    # Create a Spark session
    spark = SparkSession.builder.appName("UserRecommendation").getOrCreate()

    loaded_model = ALSModel.load("../models/als_model")

    # Generate top N movie recommendations for the specified user
    top_n = 5
    user_recommendations = loaded_model.recommendForUserSubset(spark.createDataFrame([[user_id]], ["userId"]), top_n)

    # Show the top N recommendations for the user
    user_recommendations = user_recommendations.select("recommendations.movieId", "recommendations.rating")
    user_recommendations.show(truncate=False)
    return user_recommendations