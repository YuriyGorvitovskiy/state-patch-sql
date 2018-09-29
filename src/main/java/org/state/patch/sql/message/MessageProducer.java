package org.state.patch.sql.message;

import org.state.patch.sql.config.TopicProducerConfig;
import org.state.patch.sql.message.kafka.KafkaMessageProducer;
import org.state.patch.sql.translator.JsonTranslator;

public interface MessageProducer<M, J extends JsonMessage> {

    public static <M, J extends JsonMessage> MessageProducer<M, J> create(TopicProducerConfig config,
                                                                          JsonTranslator<M, J> translator) {
        if (KafkaMessageProducer.NAME.equals(config.engine)) {
            return new KafkaMessageProducer<M, J>(config, translator);
        }
        throw new RuntimeException("Unkown patch topic engine: " + config.engine);
    }

    public void post(M notify) throws Exception;

}
