# Maven Docker Image with Aliyun Mirror:
FROM registry.cn-hangzhou.aliyuncs.com/acs/maven:3-jdk-8 AS mtm-maven
# You can also use the Official Image:
#FROM maven:3.8.4-jdk-11-slim AS mtm-maven

WORKDIR /tmp
ADD pom.xml /tmp
RUN ["/usr/local/bin/mvn-entrypoint.sh", "mvn", "clean", "package", "--fail-never"]

ADD . /tmp
RUN ["/usr/local/bin/mvn-entrypoint.sh", "mvn", "package", "-P prod"]

FROM anapsix/alpine-java:8u202b08_server-jre
# Alternatives to start a Java instance:
#FROM openjdk:8-jre-alpine
#FROM openjdk:11.0.2-jre-slim

COPY --from=mtm-maven /tmp/target/mtm-1.0.jar /app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
CMD ["--spring.profiles.active=prod"]