FROM openjdk:17-jdk
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
RUN mkdir -p /pdf
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","/app.jar"]