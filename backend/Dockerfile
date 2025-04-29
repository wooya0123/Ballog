FROM gradle:7.6.1-jdk17 AS build
WORKDIR /app
# 먼저 의존성 파일만 복사해서 캐시 활용
COPY build.gradle settings.gradle ./
# 의존성 다운로드 시 네트워크 타임아웃 증가 및 재시도 설정
RUN gradle dependencies --no-daemon --stacktrace --info \
    --build-cache \
    -Dorg.gradle.internal.http.socketTimeout=120000 \
    -Dorg.gradle.internal.http.connectionTimeout=120000 \
    -Dorg.gradle.internal.repository.max.retries=10 \
    -Dorg.gradle.internal.repository.initial.backoff=500

# 그 다음 소스 코드 복사
COPY . .
RUN gradle build -x test --no-daemon --stacktrace

FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]