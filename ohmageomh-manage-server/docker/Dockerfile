FROM java:openjdk-8-jre
MAINTAINER Jared Sieling <jared@smarttechlabs.com>

ENV SERVER_PREFIX /opt/omh-dsu-ri/manage-server

RUN mkdir -p $SERVER_PREFIX
ADD ohmageomh-manage-server.war $SERVER_PREFIX/
EXPOSE 8080

CMD /usr/bin/java -jar $SERVER_PREFIX/ohmageomh-manage-server.war
