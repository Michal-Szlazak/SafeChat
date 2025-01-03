FROM openjdk:21 AS builder
WORKDIR /my-project

COPY gradle/ gradle/
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .

RUN ./gradlew clean bootJar


FROM openjdk:21
WORKDIR /my-project
COPY --from=build build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]