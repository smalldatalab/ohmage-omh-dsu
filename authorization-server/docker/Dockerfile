FROM java:openjdk-8-jre
MAINTAINER Jared Sieling <jared.sieling@gmail.com>

ENV SERVER_PREFIX /opt/omh-dsu-ri/authorization-server
 
RUN mkdir -p $SERVER_PREFIX
ADD authorization-server.jar $SERVER_PREFIX/
EXPOSE 8082
VOLUME ["/var/log/ohmage-auth"]

CMD /usr/bin/java -jar $SERVER_PREFIX/authorization-server.jar --spring.config.location=file:$SERVER_PREFIX/
