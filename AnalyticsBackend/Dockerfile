# Multi-stage build: build JAR with Maven, then run on JRE
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -q -B -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -B -DskipTests clean package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/target/analytics-backend-2.1.0.jar /app/app.jar

ENV JAVA_OPTS=""
ENV SPRING_PROFILES_ACTIVE=dev

EXPOSE 8085

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]

