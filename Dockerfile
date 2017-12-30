FROM openjdk:8-jdk-alpine

EXPOSE 8080

WORKDIR memes-api

ADD build.gradle /memes-api
ADD gradlew /memes-api
ADD gradlew.bat /memes-api
ADD settings.gradle /memes-api
ADD README.md /memes-api
ADD LICENSE /memes-api
ADD src /memes-api/src
ADD gradle /memes-api/gradle

RUN ./gradlew assemble

CMD ["java", "-jar", "/memes-api/build/libs/memes-api-0.1.7.jar"]