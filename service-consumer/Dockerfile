# syntax=docker/dockerfile:1

# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
# Copy the whole repository to preserve module layout for Maven reactor
COPY . .
RUN mvn -q -DskipTests -pl service-consumer -am package spring-boot:repackage

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/service-consumer/target/service-consumer-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
