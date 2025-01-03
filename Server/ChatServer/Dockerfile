FROM openjdk:21 AS build
WORKDIR /my-project

COPY gradle/ gradle/
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x gradlew
RUN ./gradlew clean bootJar


FROM openjdk:21
WORKDIR /my-project
COPY --from=build /my-project/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]