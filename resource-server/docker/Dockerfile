FROM java:openjdk-8-jre
MAINTAINER Jared Sieling <jared.sieling@gmail.com>

ENV SERVER_PREFIX /opt/omh-dsu-ri/resource-server
 
RUN mkdir -p $SERVER_PREFIX
ADD resource-server.jar $SERVER_PREFIX/
EXPOSE 8083
VOLUME ["/var/log/ohmage-resource"]

CMD /usr/bin/java -jar $SERVER_PREFIX/resource-server.jar --spring.config.location=file:$SERVER_PREFIX/
