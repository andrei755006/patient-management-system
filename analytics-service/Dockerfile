# Runtime stage using JDK 21
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

# Copy the pre-built jar from your local target folder
# After running 'mvn clean package' locally
COPY target/*.jar app.jar

# HTTP port for REST API and Swagger UI
EXPOSE 4002
# gRPC port for analytics data collection
EXPOSE 9002

ENTRYPOINT ["java", "-jar", "app.jar"]



#FROM maven:3.9.9-eclipse-temurin-21 AS builder
#
#WORKDIR /app
#
## Update and upgrade packages
#RUN apt-get update && apt-get upgrade -y
#
#COPY pom.xml .
#RUN mvn dependency:go-offline
#COPY src ./src
#RUN mvn clean package
#
#FROM eclipse-temurin:21-jdk AS runner
#
#WORKDIR /app
#
#COPY --from=builder /app/target/analytics-service-0.0.1-SNAPSHOT.jar ./app.jar
#
#EXPOSE 4002
#
## Опционально можно обновить пакеты и здесь
#RUN apt-get update && apt-get upgrade -y && apt-get clean
#
#ENTRYPOINT ["java", "-jar", "./app.jar"]
