package org.state.patch.sql.config;

import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.state.patch.sql.consumer.KafkaPatchConsumer;

public class PatchTopicConfig {

    public String engine = KafkaPatchConsumer.NAME;

    public String topic = "state-patch";

    public int partition = 0;

    @SuppressWarnings("serial")
    public Properties consumer = new Properties() {
        {
            try {
                this.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
                this.put(ConsumerConfig.GROUP_ID_CONFIG, "state-patch-sql");
                this.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
                this.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
            } catch (Throwable ex) {
                throw new RuntimeException(ex);
            }
        }
    };
}
