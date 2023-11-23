# Use the official Python base image
FROM python:3.8

# Set the working directory within the container
WORKDIR /app

# Copy your Python script into the container
COPY . /app/

# Copy Kafka config into the container
COPY ./kafka_config.ini /app/

# Run the Python script when the container starts
CMD ["python", "recommend_api.py"]
