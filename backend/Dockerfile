FROM gradle:7.6.1-jdk17 AS build
WORKDIR /app

ARG SPRING_BOOT_VERSION="3.4.5"
ARG JAVA_VERSION=17

# 먼저 의존성 파일만 복사
COPY build.gradle settings.gradle ./

# build.gradle에서 스프링 부트 버전 설정
RUN sed -i "s/springBootVersion = '.*'/springBootVersion = '${SPRING_BOOT_VERSION}'/" build.gradle || echo "Spring Boot version not found in build.gradle"

# 의존성 다운로드
RUN gradle dependencies --no-daemon --build-cache \
    -Dorg.gradle.internal.http.socketTimeout=120000 \
    -Dorg.gradle.internal.http.connectionTimeout=120000 \
    -Dorg.gradle.internal.repository.max.retries=10

# 소스 코드 복사 및 빌드
COPY . .
RUN gradle build -x test --no-daemon

FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]