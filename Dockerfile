# Build stage: Maven builds the React app (-Pfrontend) into the jar's static/ folder.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
COPY frontend ./frontend
RUN mvn -B -Pfrontend package -DskipTests

# Runtime stage: JRE only. Must be 21 to match the compiled bytecode.
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENV SPRING_PROFILES_ACTIVE=demo
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "app.jar"]
