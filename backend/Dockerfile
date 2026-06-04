FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/backend-0.0.1-SNAPSHOT.jar app.jar
RUN mkdir logs
EXPOSE 8999
ENTRYPOINT ["java", "-jar", "app.jar"]