package org.state.patch.sql.message.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.state.patch.sql.config.MessageProducerConfig;
import org.state.patch.sql.message.JsonMessage;
import org.state.patch.sql.message.MessageProducer;
import org.state.patch.sql.translator.JsonTranslator;

import com.fasterxml.jackson.databind.ObjectMapper;

public class KafkaMessageProducer<M, J extends JsonMessage> implements MessageProducer<M, J> {

    public static final String NAME        = "KAFKA";
    static final String        MESSAGE_KEY = "Notify.JSON";

    public final MessageProducerConfig         config;
    public final JsonTranslator<M, J>          translator;
    public final ObjectMapper                  mapper;
    public final KafkaProducer<String, byte[]> producer;

    public KafkaMessageProducer(MessageProducerConfig config, JsonTranslator<M, J> translator) {
        this.config = config;
        this.translator = translator;

        this.mapper = new ObjectMapper();
        // No implicit nulls
        // this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

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
