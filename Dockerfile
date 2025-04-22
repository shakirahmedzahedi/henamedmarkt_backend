#FROM openjdk:17-jdk-slim as builder
#
#WORKDIR /app
#
#COPY pom.xml ./
#COPY .mvn .mvn
#COPY mvnw ./
#
#RUN chmod +x mvnw
#COPY src ./src
#
#RUN ./mvnw clean package -DskipTests
#
#FROM openjdk:17-jdk-slim
#
#WORKDIR /app
#
#COPY --from=builder /app/target/goat-0.0.1-SNAPSHOT.jar app.jar
#
#EXPOSE 8080
#
#ENTRYPOINT ["java", "-jar", "app.jar"]

# Stage 1: Build the application
FROM openjdk:17-jdk-slim as builder

WORKDIR /app

# Copy the Maven wrapper and pom.xml
COPY pom.xml ./
COPY .mvn .mvn
COPY mvnw ./

# Grant execute permissions to Maven wrapper
RUN chmod +x mvnw

# Copy the source code
COPY src ./src

# Build the app (skip tests in production)
RUN ./mvnw clean package -DskipTests

# Stage 2: Create the runtime image
FROM openjdk:17-jdk-slim

WORKDIR /app

# ✅ Install jq
RUN apt-get update && apt-get install -y jq && apt-get clean

# Copy the built jar from the builder stage
COPY --from=builder /app/target/goat-0.0.1-SNAPSHOT.jar app.jar

# Copy the entrypoint script into the container
COPY entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh

# Expose port 8080
EXPOSE 8080

# Use the entrypoint script to set up Firebase and run the app
ENTRYPOINT ["/app/entrypoint.sh"]
