package org.state.patch.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.util.Collections;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.state.patch.sql.config.Configurator;
import org.state.patch.sql.config.ServiceConfig;

public class Main {
    public static void main(String[] args) {
        System.out.println("I'm state-patch-sql");

        ServiceConfig config = Configurator.extract("org.state.patch.sql", new ServiceConfig());
        try (BasicDataSource datasource = new BasicDataSource()) {
            datasource.setDriverClassName(config.database.driver);
            datasource.setUrl(config.database.url);
            datasource.setUsername(config.database.username);
            datasource.setPassword(config.database.password);
            try (Connection connection = datasource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement("SELECT ?")) {
                    statement.setString(1, "Hello Postgres!");
                    try (ResultSet result = statement.executeQuery()) {
                        while (result.next()) {
                            System.out.println("Result: " + result.getString(1));
                        }
                    }
                }
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        try (Consumer<String, String> consumer = new KafkaConsumer<>(config.patchtopic.consumer)) {
            // consumer.subscribe(Collections.singleton(config.patchtopic.topic));

            for (PartitionInfo info : consumer.partitionsFor(config.patchtopic.topic)) {
                TopicPartition tp = new TopicPartition(config.patchtopic.topic, info.partition());
                consumer.assign(Collections.singleton(tp));
                consumer.seek(tp, 0L);
            }

            final int giveUp = 10;
            int noRecordsCount = 0;

            while (noRecordsCount < giveUp) {
                final ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(1000));
                if (consumerRecords.count() == 0) {
                    noRecordsCount++;
                    continue;
                }

                consumerRecords.forEach(record -> {
                    System.out.printf("Consumer Record: (%s, %s, %d, %d)\n",
                        record.key(), record.value(),
                        record.partition(), record.offset());
                });

                consumer.commitSync();
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        System.out.println("Execution Completted.");
    }
}
