FROM openjdk:17.0.2-slim
LABEL authors="vuodo"

COPY target/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]