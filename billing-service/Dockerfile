# Using JDK 21 on Jammy (Ubuntu) for stability
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

# Copy the pre-built jar from your local target folder
# Make sure you ran 'mvn clean package' in the billing-service folder first
COPY target/*.jar app.jar

# Expose HTTP port for REST API
EXPOSE 4001
# Expose gRPC port for internal communication
EXPOSE 9001

ENTRYPOINT ["java", "-jar", "app.jar"]






#FROM maven:3.9.9-eclipse-temurin-21 AS builder
#
#WORKDIR /app
#
#COPY pom.xml .
#RUN mvn dependency:go-offline
#COPY src ./src
#
#RUN mvn clean package
#
#
#FROM eclipse-temurin:21-jdk AS runner
#
#WORKDIR /app
#
#COPY --from=builder ./app/target/billing-service-0.0.1-SNAPSHOT.jar ./app.jar
#
#EXPOSE 4001
#EXPOSE 9001
#
#ENTRYPOINT [ "java", "-jar", "./app.jar"]