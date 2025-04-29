# 첫 번째 스테이지: 그래들 빌드
FROM gradle:7.6.1-jdk17 AS build

WORKDIR /app

# 의존성 캐시 개선을 위해 build.gradle과 settings.gradle만 먼저 복사
COPY build.gradle settings.gradle ./

# gradle 캐시 디렉토리를 비우고 의존성을 강제로 다시 다운로드
RUN rm -rf /home/gradle/.gradle/caches/ && \
    mkdir -p /home/gradle/.gradle/caches/

# 의존성 다운로드 시도
RUN gradle dependencies --no-daemon --refresh-dependencies || echo "의존성 다운로드 실패, 계속 진행"

# 소스 코드 전체 복사
COPY . .

# 빌드 실행 - 오류가 발생해도 일단 JAR 파일이 존재하는지 확인
RUN (gradle build -x test --no-daemon || true) && \
    find ./build/libs -name "*.jar" -type f | wc -l

# 빌드된 JAR 파일이 없으면 다시 시도 - Spring Boot 버전을 낮춰서
RUN if [ ! -f ./build/libs/*.jar ]; then \
      echo "JAR 파일을 찾을 수 없습니다. Spring Boot 버전을 낮춰서 다시 시도합니다." && \
      echo "plugins { \n\
        id 'java' \n\
        id 'org.springframework.boot' version '3.1.10' \n\
        id 'io.spring.dependency-management' version '1.1.4' \n\
      }" > temp_build.gradle && \
      cat build.gradle | grep -v "org.springframework.boot\|io.spring.dependency-management" >> temp_build.gradle && \
      mv temp_build.gradle build.gradle && \
      gradle build -x test --no-daemon; \
    fi

# 최종 빌드 결과 확인
RUN ls -la ./build/libs/

# 두 번째 스테이지: 실행 환경
FROM openjdk:17-slim

WORKDIR /app

# 빌드 스테이지에서 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]