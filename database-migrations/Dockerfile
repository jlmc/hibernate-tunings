FROM maven:3.6.3-jdk-11
MAINTAINER Joao Costa <@costajlmpp>

RUN mkdir -p /costax/migration
WORKDIR /costax

VOLUME /costax

# Add wait-for-it
COPY wait-for-it.sh wait-for-it.sh
RUN chmod a+rwx wait-for-it.sh

COPY pom-no-parent.xml pom.xml
COPY migration migration

#ENTRYPOINT ["./wait-for-it.sh", "postgres-db:5432", "-t", "50", "--", "mvn", "flyway:migrate", "-Dflyway.url=jdbc:postgresql://postgres-db:5432/postgresdemos", "-Dflyway.schemas=public,communication,exchange,workarounds,tasks,nplusonetoone,multimedia", "-Dflyway.locations=filesystem:migration", "-Dflyway.user=postgres", "-Dflyway.password=postgres", "-Dflyway.cleanDisabled=false"]
