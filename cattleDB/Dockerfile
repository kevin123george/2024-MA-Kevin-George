# Dockerfile for Spring Boot Application using Gradle
# Use an official OpenJDK image as a parent image
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the build/libs directory to the container
COPY build/libs/cattleDB-0.0.1-SNAPSHOT.jar /app/cattleDB.jar

# Expose port 8080
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/cattleDB.jar"]
