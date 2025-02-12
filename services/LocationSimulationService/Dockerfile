# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy Gradle wrapper and build files
#COPY gradlew gradlew
#COPY gradle gradle
#COPY build.gradle build.gradle
#COPY settings.gradle settings.gradle
#
## Copy the application source code
#COPY src src
#
## Build the application
#RUN ./gradlew build

# Copy the built JAR file into the container
COPY build/libs/LocationSimulationService*.war app.jar

# Make port 8080 available to the world outside this container
EXPOSE 9094

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]