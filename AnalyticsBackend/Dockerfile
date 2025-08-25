FROM eclipse-temurin:17-jre

WORKDIR /app

# copy jar at build time
ARG JAR_FILE=target/analytics-backend-2.1.0.jar
COPY ${JAR_FILE} app.jar

# simple runtime envs
ENV JAVA_OPTS=""
ENV SPRING_PROFILES_ACTIVE=dev

EXPOSE 8085

# simple start
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]

