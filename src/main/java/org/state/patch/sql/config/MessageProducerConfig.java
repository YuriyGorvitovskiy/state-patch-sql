package org.state.patch.sql.config;

import java.util.Properties;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.state.patch.sql.message.kafka.KafkaMessageProducer;

public class MessageProducerConfig {

    public String engine = KafkaMessageProducer.NAME;

    public String topic = "state-notify";

    @SuppressWarnings("serial")
    public Properties properties = new Properties() {
        {
            this.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            this.put(ProducerConfig.ACKS_CONFIG, "all");
            this.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, "30000");
            this.put(ProducerConfig.RETRIES_CONFIG, "5");
            this.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "1");
            this.put(ProducerConfig.BATCH_SIZE_CONFIG, "16384");
            this.put(ProducerConfig.LINGER_MS_CONFIG, "1");
            this.put(ProducerConfig.BUFFER_MEMORY_CONFIG, "67108864");
            this.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            this.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
        }
    };
}
