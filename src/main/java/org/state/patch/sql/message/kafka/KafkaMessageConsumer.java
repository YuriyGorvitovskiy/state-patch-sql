package org.state.patch.sql.message.kafka;

import java.time.Duration;
import java.util.Collections;
import java.util.function.Consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.state.patch.sql.config.MessageConsumerConfig;
import org.state.patch.sql.message.JsonMessage;
import org.state.patch.sql.message.MessageConsumer;
import org.state.patch.sql.translator.JsonTranslator;

import com.fasterxml.jackson.databind.ObjectMapper;

public class KafkaMessageConsumer<M, J extends JsonMessage> implements MessageConsumer<M, J> {

    public static final String NAME = "KAFKA";

    public final MessageConsumerConfig config;
    public final JsonTranslator<M, J>  translator;
    public final Class<J>              messageClass;
    public final ObjectMapper          mapper;

    public final long CONTINUE_OFFSET = -1L;

    public KafkaMessageConsumer(MessageConsumerConfig config, JsonTranslator<M, J> translator) {
        this.config = config;
        this.translator = translator;
        this.messageClass = translator.getJsonClass();
        this.mapper = new ObjectMapper();
    }

    @Override
    public void run(Consumer<M> processor) {
        run(CONTINUE_OFFSET, processor);
    };

    void run(long offset, Consumer<M> processor) {
        try (KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<>(config.properties)) {
            // consumer.subscribe(Collections.singleton(config.patchtopic.topic));

            TopicPartition tp = new TopicPartition(config.topic, config.partition);
            consumer.assign(Collections.singleton(tp));

            if (CONTINUE_OFFSET != offset) {
                consumer.seek(tp, offset);
            }

            while (true) {
                for (ConsumerRecord<String, byte[]> record : consumer.poll(Duration.ofMillis(1000L))) {
                    J json = mapper.readValue(record.value(), messageClass);
                    json.message_id = record.offset();
                    M message = translator.fromJson(json);
                    processor.accept(message);
                }
                consumer.commitSync();
            }
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }
}
