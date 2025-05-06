FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Create logs directory
RUN mkdir -p logs

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod

# Expose the port
EXPOSE 5555

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]