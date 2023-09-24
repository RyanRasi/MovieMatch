# Use a base image with Java 11
FROM openjdk:11-jdk

# Install necessary tools and dependencies
RUN apt-get update && apt-get install -y wget tar maven

# Verify Maven installation
RUN mvn --version

# Set environment variables
ENV JAVA_HOME=/usr/local/openjdk-11
ENV PATH="$PATH:$JAVA_HOME/bin"
ENV SPARK_HOME=/opt/spark
ENV HADOOP_HOME=/opt/hadoop
ENV MAHOUT_HOME=/opt/mahout

# Set the working directory to your project folder
WORKDIR /opt/moviematch_backend

# Copy your Java project files from your local machine into the image
COPY . .

# Build Spring Boot application using Maven
RUN mvn install

# Create an entry point script to run your Java application
RUN echo '#!/bin/bash' > entrypoint.sh && \
    echo 'java -cp target/classes org.moviematch.Main' >> entrypoint.sh && \
    chmod +x entrypoint.sh

# Use the entry point script to run your application
##CMD ["/bin/bash", "/opt/moviematch_backend/entrypoint.sh"]
EXPOSE 8080
# Use the entry point script to run your application
CMD ["java", "-jar", "./target/MovieMatch-1.0-SNAPSHOT.jar"]  # Update the JAR file name
