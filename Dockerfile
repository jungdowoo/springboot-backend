# Use a base image with JDK 17
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the Maven wrapper
COPY . .

# Build the project using Maven
RUN ./mvnw clean package

# Copy the jar file into the container
COPY target/*.jar app.jar

# Expose the port the application will run on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
