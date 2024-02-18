FROM gradle:7-jdk11 AS build
# Sets ownership of the copied files to the gradle user for compatibility with Gradle
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

FROM openjdk:11
# This may seem redundant because also specified in docker-compose but beneficial when running container on its own
EXPOSE 8080:8080
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/ktor-mandalorian-api.jar
ENTRYPOINT ["java","-jar","/app/ktor-mandalorian-api.jar"]