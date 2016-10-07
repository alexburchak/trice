FROM ubuntu:16.04

RUN apt-get update && \
    apt-get install -y openjdk-8-jdk-headless && \
    apt-get install -y iproute2

COPY bin/trice.sh /usr/lib/trice/bin/trice.sh
RUN chmod a+rx /usr/lib/trice/bin/trice.sh

RUN mkdir /usr/lib/trice/logs

COPY conf/application.yml /usr/lib/trice/conf/application.yml

COPY lib/trice.jar /usr/lib/trice/lib/trice.jar

COPY docker-entrypoint.sh /

ENTRYPOINT /docker-entrypoint.sh

EXPOSE 8081
