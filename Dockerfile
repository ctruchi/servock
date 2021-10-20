FROM openjdk:16

RUN mkdir /opt/servock
COPY build/libs/servock-*.jar /opt/servock/servock.jar
COPY docker/servock.sh /opt/servock/servock.sh

EXPOSE 8080

ENTRYPOINT ["/opt/servock/servock.sh"]
