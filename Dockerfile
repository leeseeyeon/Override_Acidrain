# 1. Maven과 Java 21을 사용하여 프로젝트 빌드
FROM maven:3.9-eclipse-temurin-21 AS build

//Docker 내부 작업 폴더 설정
WORKDIR /app

# 2. Maven 설정 파일 복사
COPY pom.xml .

# 3. 프로젝트 의존성 다운로드
RUN mvn dependency:go-offline

# 4. 소스 코드 복사
COPY src ./src

# 5. 프로젝트 빌드 및 실행에 필요한 라이브러리 복사
RUN mvn clean package -DskipTests dependency:copy-dependencies -DincludeScope=runtime

# 6. 서버 실행 환경 구성
FROM eclipse-temurin:21-jre

WORKDIR /app

# 7. 빌드 결과 및 외부 라이브러리 복사
COPY --from=build /app/target/classes ./classes
COPY --from=build /app/target/dependency ./lib

# 8. 서버 내부 포트 설정
EXPOSE 50023

# 9. 서버 프로그램 실행
CMD ["java", "-cp", "classes:lib/*", "server.network.ServerMain"]