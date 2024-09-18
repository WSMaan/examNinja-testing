FROM openjdk:17
COPY ./target/hello-world-1.0-SNAPSHOT.jar /app/hello-world.jar
WORKDIR /app
CMD ["java", "-jar", "hello-world.jar"]
