# Use a base image with JDK 17
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the Maven wrapper and project files into the container
COPY . .

# Give execution permissions to Maven wrapper
RUN chmod +x ./mvnw

# Build the project using Maven
RUN ./mvnw clean package

# 4. 애플리케이션 빌드 (테스트 건너뜀)
RUN ./mvnw clean package -Dmaven.test.skip=true

# Copy the jar file into the container
COPY target/*.jar app.jar

# Expose the port the application will run on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
