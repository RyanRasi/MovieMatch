from confluent_kafka import Producer
import configparser

def producer(message):
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

        # Produce the message to the Kafka topic
        producer.produce(topic, value=message)

        # Flush the producer to make sure all messages are sent
        producer.flush()
        return "Message successfully sent"
    except:
        return "Message failed to send"