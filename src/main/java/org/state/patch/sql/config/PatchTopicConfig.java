package org.state.patch.sql.config;

import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

public class PatchTopicConfig {

    public String engine = "KAFKA";

    public String topic = "state-patch";

    @SuppressWarnings("serial")
    public Properties consumer = new Properties() {
        {
            try {
                this.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
                this.put(ConsumerConfig.GROUP_ID_CONFIG, "state-patch-sql");
                this.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
                this.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            } catch (Throwable ex) {
                throw new RuntimeException(ex);
            }
        }
    };
}
