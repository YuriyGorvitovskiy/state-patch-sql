#!/bin/sh

cd /app

JAVA_OPS=${JAVA_OPS} -Dorg.state.patch.sql.model.database

CLASSPATH="."
CLASSPATH="${CLASSPATH}:commons-dbcp2-2.5.0.jar"
CLASSPATH="${CLASSPATH}:commons-io-2.6.jar"
CLASSPATH="${CLASSPATH}:commons-lang3-3.8.jar"
CLASSPATH="${CLASSPATH}:commons-logging-1.2.jar"
CLASSPATH="${CLASSPATH}:commons-pool2-2.6.0.jar"
CLASSPATH="${CLASSPATH}:jackson-annotations-2.9.6.jar"
CLASSPATH="${CLASSPATH}:jackson-core-2.9.6.jar"
CLASSPATH="${CLASSPATH}:jackson-databind-2.9.6.jar"
CLASSPATH="${CLASSPATH}:kafka-clients-2.0.0.jar"
CLASSPATH="${CLASSPATH}:lz4-java-1.4.1.jar"
CLASSPATH="${CLASSPATH}:postgresql-42.2.4.jar"
CLASSPATH="${CLASSPATH}:slf4j-api-1.7.25.jar"
CLASSPATH="${CLASSPATH}:snappy-java-1.1.7.1.jar"
CLASSPATH="${CLASSPATH}:state-patch-sql.jar"

echo "/usr/bin/java ${JAVA_OPS} -classpath ${CLASSPATH} org.state.patch.sql.Main"

/usr/bin/java ${JAVA_OPS} -classpath ${CLASSPATH} org.state.patch.sql.Main
