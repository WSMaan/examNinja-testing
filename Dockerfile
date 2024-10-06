# Stage 1: Build
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package

# Stage 2: Run
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/ExamNinja-1.0-SNAPSHOT.jar app.jar

# Expose the port your application runs on (if applicable)
EXPOSE 8080

# Set the entry point to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

# Optional: Add a health check
HEALTHCHECK CMD curl --fail http://localhost:8080/actuator/health || exit 1
