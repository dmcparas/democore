# Stage 1: Build the JAR using Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run the JAR with OpenJDK
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Expose Spring Boot and gRPC ports
EXPOSE 8080
#EXPOSE 9090

# Run the application and bind to all interfaces
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=8080", "--server.address=0.0.0.0"]