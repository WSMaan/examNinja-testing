FROM openjdk:17
COPY ./target/examNinja_frontend-1.0-SNAPSHOT.jar /app/examNinja_frontend.jar
WORKDIR /app
CMD ["java", "-jar", "examNinja_frontend.jar"]
