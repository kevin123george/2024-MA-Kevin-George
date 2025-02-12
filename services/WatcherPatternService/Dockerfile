# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy Gradle wrapper and build files

# Copy the application source code
COPY src src

# Copy the built JAR file into the container
COPY build/libs/WatcherPatternService-0.0.1-SNAPSHOT.war app.jar

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
