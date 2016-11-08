FROM java:openjdk-8-jre
MAINTAINER Samuel Kurath <skurath@hsr.ch>

ENV SCALA_VERSION 2.11
ENV KAFKA_VERSION 0.10.0.1
ENV KAFKA_HOME /opt/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION"

COPY entrypoint.sh /opt/OSMstream/
COPY supervisord.conf /etc/supervisor/conf.d/supervisord.conf

ENTRYPOINT ["./opt/OSMstream/entrypoint.sh"]

RUN apt-get update \
    && apt-get install -y curl zookeeper vim cron supervisor\
    && wget -q http://apache.mirrors.spacedump.net/kafka/"$KAFKA_VERSION"/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION".tgz -O /tmp/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION".tgz \
    && tar xfz /tmp/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION".tgz -C /opt \
    && chmod +x /opt/OSMstream/entrypoint.sh \
    && cd /opt \
    && wget -q http://mirror.easyname.ch/apache/storm/apache-storm-1.0.2/apache-storm-1.0.2.tar.gz \
    && tar xfz apache-storm-1.0.2.tar.gz \