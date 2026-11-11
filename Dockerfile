# El JAR llega precompilado desde GitHub Actions
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY app.jar app.jar
EXPOSE 9005
ENTRYPOINT ["java", "-Xmx512m", "-jar", "app.jar"]
