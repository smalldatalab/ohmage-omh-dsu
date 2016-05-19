FROM java:openjdk-8-jre
MAINTAINER Jared Sieling <jared@smarttechlabs.com>

ENV SERVER_PREFIX /opt/omh-dsu-ri/resource-server
 
RUN mkdir -p $SERVER_PREFIX
ADD ohmageomh-resource-server.jar $SERVER_PREFIX/
EXPOSE 8083

CMD /usr/bin/java -jar $SERVER_PREFIX/ohmageomh-resource-server.jar --spring.config.location=file:$SERVER_PREFIX/
