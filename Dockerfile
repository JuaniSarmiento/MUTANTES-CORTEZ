# Etapa de compilación
FROM maven:3.9.5-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa de ejecución con imagen optimizada
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/mutant-detector-1.0.0.jar app.jar
ENV SERVER_PORT=8080
ENV JAVA_OPTS="-Xmx512m -Xms256m"
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
