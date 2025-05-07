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
RUN mkdir -p metadata

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xms512m -Xmx1g"

# Expose the port
EXPOSE 5555

# Command to run the application with schema initialization
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]