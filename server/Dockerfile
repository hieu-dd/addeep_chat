FROM openjdk:11-jre-slim

WORKDIR /app

EXPOSE 8080

ADD ./build/libs/server.jar .

CMD ["java", "-jar", "server.jar"]