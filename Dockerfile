FROM amazoncorretto:17
LABEL maintainer="rjsckdd12@gmail.com"
COPY build/libs/*SNAPSHOT.jar app.jar

ENV USE_PROFILE dep

ENV JAVA_TOOL_OPTIONS "-Dspring.profiles.active=${USE_PROFILE}"

ENTRYPOINT ["java","-jar","app.jar"]
