# --- Build stage ---
FROM maven:3.9-amazoncorretto-17 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -q

COPY src ./src
RUN mvn package -DskipTests -q

# --- Runtime stage ---
FROM amazoncorretto:17-alpine

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=builder /app/target/flow-no-code-0.0.1-SNAPSHOT.jar app.jar

RUN chown spring:spring app.jar

USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
