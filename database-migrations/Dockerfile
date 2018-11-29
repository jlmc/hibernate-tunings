FROM maven:3.3.9-jdk-8-alpine

RUN mkdir -p /costax/migration
WORKDIR /costax

COPY pom.xml .
COPY migration/ migration

CMD ["/bin/sh"]
