FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine AS runner

WORKDIR /app

RUN addgroup -S app && adduser -S -G app app && mkdir -p /app/uploads && chown app:app /app/uploads

COPY --from=builder --chown=app:app /build/target/*SNAPSHOT.jar app.jar

USER app

EXPOSE ${SERVER_PORT:-8080}

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
