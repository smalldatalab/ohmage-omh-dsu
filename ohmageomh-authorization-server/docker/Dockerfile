FROM java:openjdk-8-jre
MAINTAINER Jared Sieling <jared@smarttechlabs.com>

ENV SERVER_PREFIX /opt/omh-dsu-ri/authorization-server
 
RUN mkdir -p $SERVER_PREFIX
ADD ohmageomh-authorization-server.jar $SERVER_PREFIX/
EXPOSE 8082

CMD /usr/bin/java -jar $SERVER_PREFIX/ohmageomh-authorization-server.jar --spring.config.location=file:$SERVER_PREFIX/
