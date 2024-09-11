# Use official OpenJDK image as a parent image
FROM gradle:7.6-jdk17-alpine AS build

# Set the working directory inside the container
WORKDIR /app

# Copy Gradle wrapper and project files to the container
COPY --chown=gradle:gradle . .

# Build the project using Gradle
RUN gradle build --no-daemon

# Stage 2: Use a minimal JDK image to run the application
FROM openjdk:17-jdk-alpine

# Set the working directory for the app
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/build/libs/cattleDB-0.0.1-SNAPSHOT.jar /app/cattleDB.jar

# Expose port 8080 for the Spring Boot app
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/cattleDB.jar"]
