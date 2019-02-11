FROM openjdk:8-jre

MAINTAINER dockers@logistimo.com


ENV HOME /root

ARG APP_NAME
ARG APP_VERSION

ADD ${APP_NAME}-${APP_VERSION}.jar $HOME/communication-service.jar

RUN bash -c 'touch $HOME/communication-service.jar'

ENV JAVA_OPTS="-Xms512M -Xmx1G" \
	SERVER_PORT=9020 \
	MYSQL_COMM_HOST=jdbc:mariadb://localhost:3306/communications?useUnicode=true&amp;characterEncoding=UTF-8 \
	MYSQL_COMM_USER=logistimo \
	MYSQL_COMM_PASS=logistimo \
	ACTIVEMQ_URL=tcp://localhost:61616 \
	ACTIVEMQ_USER=admin \
	ACTIVEMQ_PASS=admin \
	REDIS_HOST=localhost \
	REDIS_PORT=6379 \
	SENTINEL_HOST=localhost:26379,localhost:26378 \
	SENTINEL_MASTER=mymaster \
	ISSENTINEL=false \
	JAVA_AGENT_PORT=9099 \
	GOOGLE_APPLICATION_CREDENTIALS="/root/fcm-service.json" \
	FIREBASE_CONFIG="/root/fcm-db.json" \
	SERVICE_NAME=logi-communication \
	APM_SERVER_URL=localhost:8200

RUN wget -P $HOME/ http://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/0.7/jmx_prometheus_javaagent-0.7.jar \
	&& wget -P $HOME/ http://repo1.maven.org/maven2/co/elastic/apm/elastic-apm-agent/0.6.0/elastic-apm-agent-0.6.0.jar

ADD jmx_exporter.json $HOME/

COPY docker-entrypoint.sh /docker-entrypoint.sh

RUN chmod +x /docker-entrypoint.sh

EXPOSE 9090 9099

CMD ["/docker-entrypoint.sh"]
