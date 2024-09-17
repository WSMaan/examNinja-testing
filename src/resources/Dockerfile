# Use an appropriate base image, e.g., OpenJDK 17
FROM openjdk:17

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/examNinja_frontend-1.0-SNAPSHOT.jar /app/examNinja_frontend-1.0-SNAPSHOT.jar

# Expose the port the application runs on
EXPOSE 8080

# Define the command to run your application
CMD ["java", "-jar", "examNinja_frontend-1.0-SNAPSHOT.jar"]
