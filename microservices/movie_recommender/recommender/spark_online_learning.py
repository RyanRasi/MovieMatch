from pyspark.ml.recommendation import ALS
from pyspark.sql import SparkSession
from pyspark.sql.types import StructType, StructField, IntegerType, DoubleType, StringType
from pyspark.ml.evaluation import RegressionEvaluator
from pyspark.ml import PipelineModel
from pyspark.ml.feature import StringIndexer
import configparser

# Create a ConfigParser object
config = configparser.ConfigParser()

# Read the configuration file
config.read('kafka_config.ini')

# Access the Kafka configuration parameters
bootstrap_servers = config.get('Kafka', 'bootstrap_servers')
topic = config.get('Kafka', 'topic')

def update_model(existing_model, new_data):
    # Update the existing ALS model with new data
    updated_model = existing_model.fit(new_data)
    return updated_model

# Create a Spark session
spark = SparkSession.builder.appName("RealTimeRecommendation").getOrCreate()

# Load existing model
existing_model = ALS.load(f"../models/als_model")

# Define the schema for the Kafka messages
schema = StructType([
    StructField("userId", IntegerType(), True),
    StructField("movieId", IntegerType(), True),
    StructField("rating", DoubleType(), True)
])

# Read data from Kafka
kafka_df = spark \
    .readStream \
    .format("kafka") \
    .option("kafka.bootstrap.servers", bootstrap_servers) \
    .option("subscribe", topic) \
    .load() \
    .selectExpr("CAST(value AS STRING)") \
    .selectExpr("split(value, ',') as data") \
    .selectExpr("cast(data[0] as int) as userId", "cast(data[1] as int) as movieId", "cast(data[2] as double) as rating")

# Update the model based on incoming Kafka data
streaming_query = kafka_df \
    .writeStream \
    .foreachBatch(lambda batch_df, batch_id: update_model(existing_model, batch_df).save("../models/als_model")) \
    .start()

streaming_query.awaitTermination()
