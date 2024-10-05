# Stage 1: Build the application
FROM maven:3.9.7-sapmachine-22 AS build
WORKDIR /app
COPY pom.xml .
COPY ~/.m2 /root/.m2
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests
RUN echo "done"

# Stage 2: Run the application
FROM openjdk:22-slim
WORKDIR /app
COPY ./target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]