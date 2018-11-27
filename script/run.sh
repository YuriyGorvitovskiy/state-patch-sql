#!/bin/sh

cd /app

JAVA_OPS=""

JAVA_OPS="${JAVA_OPS} -Dorg.state.patch.sql.entity.database.url=jdbc:postgresql://$POSTGRES_HOST:$POSTGRES_PORT/$POSTGRES_DATABASE"
JAVA_OPS="${JAVA_OPS} -Dorg.state.patch.sql.entity.database.username=$POSTGRES_USERNAME"
JAVA_OPS="${JAVA_OPS} -Dorg.state.patch.sql.entity.database.password=$POSTGRES_PASSWORD"
JAVA_OPS="${JAVA_OPS} -Dorg.state.patch.sql.entity.database.schema=public"

JAVA_OPS="${JAVA_OPS} -Dorg.state.patch.sql.model.database.url=jdbc:postgresql://$POSTGRES_HOST:$POSTGRES_PORT/$POSTGRES_DATABASE"
JAVA_OPS="${JAVA_OPS} -Dorg.state.patch.sql.model.database.username=$POSTGRES_USERNAME"
JAVA_OPS="${JAVA_OPS} -Dorg.state.patch.sql.model.database.password=$POSTGRES_PASSWORD"
JAVA_OPS="${JAVA_OPS} -Dorg.state.patch.sql.model.database.schema=model"


JAVA_OPS="${JAVA_OPS} -Dorg.state.patch.sql.patch.properties.bootstrap.servers=$KAFKA_HOST:$KAFKA_PORT"
JAVA_OPS="${JAVA_OPS} -Dorg.state.patch.sql.patch.topic=$KAFKA_PATCH_TOPIC"
JAVA_OPS="${JAVA_OPS} -Dorg.state.patch.sql.notify.properties.bootstrap.servers=$KAFKA_HOST:$KAFKA_PORT"
JAVA_OPS="${JAVA_OPS} -Dorg.state.patch.sql.notify.topic=$KAFKA_NOTIFY_TOPIC"

CLASSPATH=""
CLASSPATH="${CLASSPATH}:commons-dbcp2-2.5.0.jar"
CLASSPATH="${CLASSPATH}:commons-io-2.6.jar"
CLASSPATH="${CLASSPATH}:commons-lang3-3.8.jar"
CLASSPATH="${CLASSPATH}:commons-logging-1.2.jar"
CLASSPATH="${CLASSPATH}:commons-pool2-2.6.0.jar"
CLASSPATH="${CLASSPATH}:jackson-annotations-2.9.7.jar"
CLASSPATH="${CLASSPATH}:jackson-core-2.9.7.jar"
CLASSPATH="${CLASSPATH}:jackson-databind-2.9.7.jar"
CLASSPATH="${CLASSPATH}:kafka-clients-2.0.0.jar"
CLASSPATH="${CLASSPATH}:lz4-java-1.4.1.jar"
CLASSPATH="${CLASSPATH}:postgresql-42.2.4.jar"
CLASSPATH="${CLASSPATH}:service-common-1.0.2.jar"
CLASSPATH="${CLASSPATH}:service-protocol-1.0.1.jar"
CLASSPATH="${CLASSPATH}:slf4j-api-1.7.25.jar"
CLASSPATH="${CLASSPATH}:snappy-java-1.1.7.1.jar"
CLASSPATH="${CLASSPATH}:state-patch-sql.jar"

echo "/usr/bin/java ${JAVA_OPS} -classpath ${CLASSPATH} org.state.patch.sql.Main"

/usr/bin/java ${JAVA_OPS} -classpath ${CLASSPATH} org.state.patch.sql.Main
