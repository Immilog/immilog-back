#!/bin/bash

# base 이미지 설정
FROM eclipse-temurin:21

# jar 파일 위치를 변수로 설정
ARG JAR_FILE=/build/libs/*-SNAPSHOT.jar

# 환경변수 설정
ENV CUSTOM_NAME default

# jar 파일을 컨테이너 내부에 복사
COPY ${JAR_FILE} immilog.jar

# 로그 디렉토리 생성
RUN mkdir -p /var/log/api

# 외부 호스트 8080 포트로 노출
EXPOSE 8080

# 실행 명령어
CMD ["java", "-Dtest.customName=${CUSTOM_NAME}", "-jar", "immilog.jar"]