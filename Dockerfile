FROM openjdk:17

# Copy the JAR file from the target folder of the backend (assuming backend project)
COPY ./target/ExamNinja2-testing-1.0-SNAPSHOT.jar /app/examNinja_testing.jar

# Set the working directory to /app
WORKDIR /app

# Command to run the JAR file
CMD ["java", "-jar", "examNinja_testing.jar"]
