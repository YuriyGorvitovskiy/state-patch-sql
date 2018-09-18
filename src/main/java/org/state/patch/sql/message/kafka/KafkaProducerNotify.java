package org.state.patch.sql.message.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.state.patch.sql.config.NotifyTopicConfig;
import org.state.patch.sql.message.ProducerNotify;
import org.state.patch.sql.model.notify.JsonNotify;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

public class KafkaProducerNotify implements ProducerNotify {

    public static final String NAME        = "KAFKA";
    static final String        MESSAGE_KEY = "Notify.JSON";

    final NotifyTopicConfig             config;
    final ObjectMapper                  mapper;
    final KafkaProducer<String, byte[]> producer;

    public KafkaProducerNotify(NotifyTopicConfig config) {
        this.config = config;

        this.mapper = new ObjectMapper();
        this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        this.producer = new KafkaProducer<>(config.producer);
    }

    @Override
    public void post(JsonNotify notify) throws Exception {
        byte[] json = mapper.writeValueAsBytes(notify);
        ProducerRecord<String, byte[]> record = new ProducerRecord<>(config.topic, MESSAGE_KEY, json);
        producer.send(record).get();
        producer.flush();
    }

}
