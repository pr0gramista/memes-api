FROM adoptopenjdk/openjdk11-openj9:alpine as builder

EXPOSE 8080

WORKDIR memes-api

ADD build.gradle /memes-api
ADD gradlew /memes-api
ADD gradlew.bat /memes-api
ADD settings.gradle /memes-api
ADD src /memes-api/src
ADD gradle /memes-api/gradle

RUN ./gradlew assemble

FROM adoptopenjdk/openjdk11-openj9:alpine
COPY --from=builder /memes-api/build/libs/memes-api.jar memes-api.jar
CMD ["java", "-jar", "memes-api.jar"]
