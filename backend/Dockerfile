# 1단계: Maven 환경에서 백엔드 소스 빌드
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml ./
COPY src ./src
RUN mvn clean package -DskipTests

# 2단계: 가벼운 실행용 JRE 이미지 탑재
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/backend-0.0.1-SNAPSHOT.jar app.jar
RUN mkdir logs
EXPOSE 8999
ENTRYPOINT ["java", "-jar", "app.jar"]