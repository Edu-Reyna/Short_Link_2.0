FROM openjdk:21-jdk-slim

ARG JAR_FILE=target/acortador-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} acortador.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/acortador.jar"]
