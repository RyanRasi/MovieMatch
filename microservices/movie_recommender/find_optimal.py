from pyspark.sql import SparkSession
from pyspark.ml.recommendation import ALS
from pyspark.ml.tuning import CrossValidator, ParamGridBuilder
from pyspark.ml.evaluation import RegressionEvaluator
from pyspark.sql import functions as F
from pyspark.ml.tuning import ParamGridBuilder, CrossValidator
from pyspark.ml.evaluation import RegressionEvaluator
from pyspark.ml.recommendation import ALS
from pyspark.sql import SparkSession

# Create a Spark session
spark = SparkSession.builder.appName("MovieRecommendation").getOrCreate()

# Assuming you have a "ratings" DataFrame with columns "userId", "movieId", and "rating"
ratings = spark.read.csv("./data/moviesdataset/ratings_small.csv", header=True, inferSchema=True)

# Split the data into training and test sets
(training, test) = ratings.randomSplit([0.7, 0.3], seed=123)

# Create an ALS model
als = ALS(userCol="userId", itemCol="movieId", ratingCol="rating", coldStartStrategy="drop")

# Define a parameter grid to search
param_grid = ParamGridBuilder() \
    .addGrid(als.rank, [5, 10, 15]) \
    .addGrid(als.maxIter, [5, 10, 15]) \
    .addGrid(als.regParam, [0.01, 0.1, 0.2]) \
    .build()

# Create an evaluator
evaluator = RegressionEvaluator(metricName="rmse", labelCol="rating", predictionCol="prediction")

# Create a cross-validator
cross_validator = CrossValidator(estimator=als,
                               estimatorParamMaps=param_grid,
                               evaluator=evaluator,
                               numFolds=5)

# Fit the model and select the best set of parameters
best_model = cross_validator.fit(training).bestModel

# Make predictions on the test set
test_predictions = best_model.transform(test)

# Evaluate the model
rmse = evaluator.evaluate(test_predictions)

# Print the best hyperparameters and RMSE
print("Best hyperparameters:")
print(f"Rank: {best_model.rank}")
print(f"Max Iterations: {best_model._java_obj.parent().getMaxIter()}")
print(f"Regularization Parameter: {best_model._java_obj.parent().getRegParam()}")
print(f"RMSE on test data = {rmse}")