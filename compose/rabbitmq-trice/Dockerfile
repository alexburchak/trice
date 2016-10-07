FROM rabbitmq:3

RUN apt-get update && \
    apt-get install -y wget && \
    apt-get install -y python

RUN service rabbitmq-server restart && \
    rabbitmq-plugins enable rabbitmq_management

CMD service rabbitmq-server restart && \
    sleep 36500d

EXPOSE 5672
EXPOSE 15672
