FROM eclipse-temurin:21-jre-alpine  # 👈 자바 21 규격 적용
WORKDIR /app
COPY target/backend-0.0.1-SNAPSHOT.jar app.jar
RUN mkdir logs
EXPOSE 8999
ENTRYPOINT ["java", "-jar", "app.jar"]