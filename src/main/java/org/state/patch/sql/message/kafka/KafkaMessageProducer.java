package org.state.patch.sql.message.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.state.patch.sql.config.TopicProducerConfig;
import org.state.patch.sql.message.JsonMessage;
import org.state.patch.sql.message.MessageProducer;
import org.state.patch.sql.translator.JsonTranslator;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

public class KafkaMessageProducer<M, J extends JsonMessage> implements MessageProducer<M, J> {

    public static final String NAME        = "KAFKA";
    static final String        MESSAGE_KEY = "Notify.JSON";

    final TopicProducerConfig           config;
    final JsonTranslator<M, J>          translator;
    final ObjectMapper                  mapper;
    final KafkaProducer<String, byte[]> producer;

    public KafkaMessageProducer(TopicProducerConfig config, JsonTranslator<M, J> translator) {
        this.config = config;
        this.translator = translator;

        this.mapper = new ObjectMapper();
        this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        this.producer = new KafkaProducer<>(config.producer);
    }

    @Override
    public void post(M notify) throws Exception {
        J json = translator.toJson(notify);
        byte[] bytes = mapper.writeValueAsBytes(json);
        ProducerRecord<String, byte[]> record = new ProducerRecord<>(config.topic, MESSAGE_KEY, bytes);
        producer.send(record).get();
        producer.flush();
    }
}
