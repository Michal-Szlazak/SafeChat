FROM gradle:jdk21 AS build
WORKDIR /my-project

COPY gradle/ gradle/
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .
COPY . .

RUN chmod +x gradlew
RUN ./gradlew clean bootJar


FROM gradle:jdk21
WORKDIR /my-project
COPY --from=build /my-project/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]