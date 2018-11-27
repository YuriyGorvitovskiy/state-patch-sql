void setBuildStatus(String message, String state) {
  step([
      $class: "GitHubCommitStatusSetter",
      reposSource: [$class: "ManuallyEnteredRepositorySource", url: "https://github.com/YuriyGorvitovskiy/state-patch-sql"],
      contextSource: [$class: "ManuallyEnteredCommitContextSource", context: "jenkins/build-status"],
      errorHandlers: [[$class: "ChangingBuildStatusErrorHandler", result: "UNSTABLE"]],
      statusResultSource: [ $class: "ConditionalStatusResultSource", results: [[$class: "AnyBuildResult", message: message, state: state]] ]
  ]);
}

pipeline {
    agent any
    options {
        skipStagesAfterUnstable()
    }
    stages {
        stage('Build Server') {
            steps {
                setBuildStatus("Build in progress...", "PENDING")

                sh './gradlew -Dtest.org.state.patch.sql.db.postgres.engine=POSTGRES -Dtest.org.state.patch.sql.db.postgres.url=jdbc:postgresql://${POSTGRES_HOST}:${POSTGRES_PORT}/${POSTGRES_DB} -Dtest.org.state.patch.sql.db.postgres.username=${POSTGRES_USERNAME} -Dtest.org.state.patch.sql.db.postgres.password=${POSTGRES_PASSWORD} -Dtest.org.state.patch.sql.db.postgres.schema=${POSTGRES_SCHEMA} -Dtest.org.state.patch.sql.message.kafka.producer.topic=${KAFKA_TOPIC} -Dtest.org.state.patch.sql.message.kafka.producer.properties.bootstrap.servers=${KAFKA_HOST}:${KAFKA_PORT} -Dtest.org.state.patch.sql.message.kafka.consumer.topic=${KAFKA_TOPIC} -Dtest.org.state.patch.sql.message.kafka.consumer.properties.bootstrap.servers=${KAFKA_HOST}:${KAFKA_PORT} clean build'
            }
        }
    }
    post {
        success {
            setBuildStatus("Build succeeded!", "SUCCESS")
        }
        failure {
            setBuildStatus("Build failed!", "FAILURE")
        }
    }
}
