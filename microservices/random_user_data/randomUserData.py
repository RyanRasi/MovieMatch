from confluent_kafka import Producer
import json, configparser, time, random

try:
    # Create a ConfigParser object
    config = configparser.ConfigParser()

    # Read the configuration file
    config.read('./kafka_config.ini')

    # Access the Kafka configuration parameters
    bootstrap_servers = config.get('Kafka', 'bootstrap_servers')
    topic = config.get('Kafka', 'topic')

    # Create a Kafka producer configuration
    producer_conf = {'bootstrap.servers': bootstrap_servers}

    # Create a Kafka producer instance
    producer = Producer(producer_conf)
except:
    print("An error occured")

# Function to send a movie event to Kafka
def send_movie_event(user_id, movie_id, rating):
    event = {'userId': user_id, 'movieId': movie_id, 'rating': rating}
    producer.produce(topic, key=str(user_id), value=json.dumps(event))
    producer.flush()

# Simulate streaming data
while True:
    # Generate random user, movie, and rating
    user_id = random.randint(1, 100)
    movie_id = random.randint(1, 1000)
    rating = random.randint(1, 5)

    # Send the movie event to Kafka
    send_movie_event(user_id, movie_id, rating)

    # Sleep for a short interval
    time.sleep(20)
