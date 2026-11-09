erfile
# ── Etapa 1: Compilación ─────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /src
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B clean package -DskipTests
 
# ── Etapa 2: Ejecución ───────────────────────────────────────────────
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /src/target/app.jar app.jar
EXPOSE 9005
ENTRYPOINT ["java", "-Xmx512m", "-jar", "app.jar"]
 