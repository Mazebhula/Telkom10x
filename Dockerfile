# Use a lightweight JDK 17 base image
FROM maven:3.9.9-eclipse-temurin-21
# Set working directory inside the container
WORKDIR /app

# Copy the Maven-built JAR file from the target directory
COPY target/form-autofill-app-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080 for the Spring Boot app
EXPOSE 8080

# Define the entry point to run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]