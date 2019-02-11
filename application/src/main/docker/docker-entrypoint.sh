#!/bin/bash -x


exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom \
    -Dserver.port=$SERVER_PORT \
    -Dspring.communication.db.url=$MYSQL_COMM_HOST \
    -Dspring.communication.db.username=$MYSQL_COMM_USER \
    -Dspring.communication.db.password=$MYSQL_COMM_PASS \
    -Dspring.activemq.broker-url=$ACTIVEMQ_URL \
    -Dspring.activemq.user=$ACTIVEMQ_USER \
    -Dspring.activemq.password=$ACTIVEMQ_PASS \
    -Dapp.redis.host=$REDIS_HOST \
    -Dapp.redis.port=$REDIS_PORT \
    -Dapp.issentinel=$ISSENTINEL \
    -Dapp.redis.sentinel.master=$SENTINEL_MASTER \
    -Dapp.redis.sentinel.nodes=$SENTINEL_HOST \
    -javaagent:$HOME/jmx_prometheus_javaagent-0.7.jar=$JAVA_AGENT_PORT:$HOME/jmx_exporter.json \
    -javaagent:$HOME/elastic-apm-agent-0.6.0.jar \
	-Delastic.apm.service_name=$SERVICE_NAME \
    -Delastic.apm.application_packages=com.logistimo.communication \
    -Delastic.apm.server_url=http://$APM_SERVER_URL \
    -DGOOGLE_APPLICATION_CREDENTIALS=$GOOGLE_APPLICATION_CREDENTIALS \
    -DFIREBASE_CONFIG=$FIREBASE_CONFIG \
    -jar $HOME/communication-service.jar
