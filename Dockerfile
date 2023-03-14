FROM amazoncorretto:17

COPY build/libs/*SNAPSHOT.jar app.jar

ENV USE_PROFILE dep

ENV JAVA_TOOL_OPTIONS "-Dspring.profiles.active=${USE_PROFILE}"

ENTRYPOINT ["java","-jar","app.jar"]
