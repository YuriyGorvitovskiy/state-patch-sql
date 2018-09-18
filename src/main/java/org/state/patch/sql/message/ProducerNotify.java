package org.state.patch.sql.message;

import org.state.patch.sql.config.NotifyTopicConfig;
import org.state.patch.sql.message.kafka.KafkaProducerNotify;
import org.state.patch.sql.notify.JsonNotify;

public interface ProducerNotify {

    public static ProducerNotify create(NotifyTopicConfig config) {
        if (KafkaProducerNotify.NAME.equals(config.engine)) {
            return new KafkaProducerNotify(config);
        }
        throw new RuntimeException("Unkown patch topic engine: " + config.engine);
    }

    public void post(JsonNotify notify) throws Exception;

}
