package org.state.patch.sql.zzz.consumer;

import java.time.Duration;
import java.util.Collections;
import java.util.function.Consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.state.patch.sql.config.PatchTopicConfig;
import org.state.patch.sql.zzz.patch.Patch;

import com.fasterxml.jackson.databind.ObjectMapper;

public class KafkaPatchConsumer implements PatchConsumer {

    public static final String NAME = "KAFKA";

    PatchTopicConfig config;

    public KafkaPatchConsumer(PatchTopicConfig config) {
        this.config = config;
    }

    @Override
    public void run(Consumer<Patch> processor) {
        try (KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<>(config.consumer)) {
            // consumer.subscribe(Collections.singleton(config.patchtopic.topic));

            TopicPartition tp = new TopicPartition(config.topic, config.partition);
            consumer.assign(Collections.singleton(tp));

            // consumer.seek(tp, 0L);

            ObjectMapper mapper = new ObjectMapper();
            while (true) {
                for (ConsumerRecord<String, byte[]> record : consumer.poll(Duration.ofMillis(1000L))) {
                    Patch patch = mapper.readValue(record.value(), Patch.class);
                    processor.accept(patch);
                }
                consumer.commitSync();
            }
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }
}
