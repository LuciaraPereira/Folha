FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /projeto

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /projeto/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]