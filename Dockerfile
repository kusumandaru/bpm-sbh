# Stage 1: Build the application
FROM maven:3.8.6-openjdk-11 AS builder

WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

# stage 2
FROM eclipse-temurin:11-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=dev"]

# # Stage 2: Run the application
# FROM eclipse-temurin:11-jre

# WORKDIR /app

# # Copy built jar from Maven stage
# COPY --from=builder /app/target/*.jar app.jar

# EXPOSE 8080

# # Recommended JVM opts for containerized Spring Boot apps
# ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]