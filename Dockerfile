# Use OpenJDK 17 as the base image
FROM openjdk:17-jdk-slim

# Set working directory inside the container
WORKDIR /app

# Copy the built JAR file into the container
COPY target/coreservice-0.0.1-SNAPSHOT.jar app.jar

# Expose default Spring Boot port
EXPOSE 8081

# Expose gRPC port (if used)
EXPOSE 9090

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
