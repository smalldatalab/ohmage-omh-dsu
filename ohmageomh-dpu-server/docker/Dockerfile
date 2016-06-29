FROM java:openjdk-8-jre
MAINTAINER Jared Sieling <jared@smarttechlabs.com>

RUN mkdir /app
WORKDIR /app

ADD dpu-server.jar /app/
EXPOSE 8081

CMD java -jar dpu-server.jar --spring.profiles.active=prod