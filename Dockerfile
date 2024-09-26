# 1. 빌드 스테이지: Maven을 사용하여 애플리케이션 빌드
FROM maven:3.8.8-openjdk-17-slim AS build

# 작업 디렉토리 설정
WORKDIR /app

# 프로젝트 파일 복사 (pom.xml과 소스 코드)
COPY pom.xml .
COPY src ./src

# Maven 빌드: 테스트를 건너뛰고 패키징
RUN mvn clean package -Dmaven.test.skip=true

# 2. 실행 스테이지: 빌드된 JAR 파일을 실행 환경으로 복사
FROM openjdk:17-jdk-alpine

# 작업 디렉토리 설정
WORKDIR /app

# 빌드 스테이지에서 JAR 파일 복사
COPY --from=build /app/target/*.jar app.jar

# 애플리케이션 포트 노출
EXPOSE 8080

# 애플리케이션 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]
