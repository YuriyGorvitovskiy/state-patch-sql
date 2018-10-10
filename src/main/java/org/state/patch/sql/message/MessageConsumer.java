package org.state.patch.sql.message;

import java.util.function.Consumer;

import org.state.patch.sql.config.MessageConsumerConfig;
import org.state.patch.sql.message.kafka.KafkaMessageConsumer;
import org.state.patch.sql.translator.JsonTranslator;

public interface MessageConsumer<M, J extends JsonMessage> {

    public static <M, J extends JsonMessage> MessageConsumer<M, J> create(MessageConsumerConfig config,
                                                                          JsonTranslator<M, J> translator) {
        if (KafkaMessageConsumer.NAME.equals(config.engine)) {
            return new KafkaMessageConsumer<M, J>(config, translator);
        }
        throw new RuntimeException("Unsupported message consumer engine: " + config.engine);
    }

    public void run(Consumer<M> processor);
}
