# Runtime stage: using a lightweight JRE image
FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Copy the pre-built jar from your local target folder
# (We use the result of your local 'mvn clean package')
COPY target/*.jar app.jar

# Important: Match the port from your application.yml (server.port: 5000)
EXPOSE 5000

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]





## Этап сборки
#FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
#WORKDIR /app
#COPY pom.xml .
#COPY src ./src
#RUN mvn clean package -DskipTests
#
## Этап запуска
#FROM eclipse-temurin:21-jre-alpine
#WORKDIR /app
#COPY --from=build /app/target/*.jar app.jar
## Копируем ключи, если они лежат в resources (они попадут внутрь jar)
#ENTRYPOINT ["java", "-jar", "app.jar"]