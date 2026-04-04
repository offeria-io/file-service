# Stage 1: Build stage
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -Dhttp.keepAlive=false -Dmaven.wagon.http.retryHandler.count=3
COPY src ./src
RUN mvn clean package -Dhttp.keepAlive=false -Dmaven.wagon.http.retryHandler.count=3 -DskipTests

# Stage 2: Production stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
EXPOSE 8084
ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:dev}", "-jar", "app.jar"]
