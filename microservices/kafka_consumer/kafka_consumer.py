from confluent_kafka import Consumer, KafkaError
import configparser, time

# Generate a unique group ID based on a prefix and timestamp
group_id = f'consumer_group_{int(time.time())}'

# Create a ConfigParser object
config = configparser.ConfigParser()

# Read the configuration file
config.read('../kafka_config.ini')

# Access the Kafka configuration parameters
bootstrap_servers = config.get('Kafka', 'bootstrap_servers')
topic = config.get('Kafka', 'topic')

# Create a Kafka consumer configuration
consumer_conf = {'bootstrap.servers': bootstrap_servers,
                 'group.id': group_id,
                 'auto.offset.reset': 'earliest'}

# Create a Kafka consumer instance
consumer = Consumer(consumer_conf)

# Subscribe to the Kafka topic
consumer.subscribe([topic])

# Poll for messages
while True:
    msg = consumer.poll(1.0)  # Adjust the timeout as needed

    if msg is None:
        continue
    if msg.error():
        if msg.error().code() == KafkaError._PARTITION_EOF:
            # End of partition event, not an error
            continue
        else:
            print(msg.error())
            break

    # Process the received message
    print(f"Received message: {msg.value().decode('utf-8')}")

# Close the consumer
consumer.close()
