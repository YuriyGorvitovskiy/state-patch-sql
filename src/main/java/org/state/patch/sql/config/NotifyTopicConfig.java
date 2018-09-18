package org.state.patch.sql.config;

import java.util.Properties;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.state.patch.sql.message.kafka.KafkaProducerNotify;

public class NotifyTopicConfig {

    public String engine = KafkaProducerNotify.NAME;

    public String topic = "state-notify";

    @SuppressWarnings("serial")
    public Properties producer = new Properties() {
        {
            try {
                this.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost::9092");
                this.put(ProducerConfig.ACKS_CONFIG, "all");
                this.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, "30000");
                this.put(ProducerConfig.RETRIES_CONFIG, "5");
                this.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "1");
                this.put(ProducerConfig.BATCH_SIZE_CONFIG, "16384");
                this.put(ProducerConfig.LINGER_MS_CONFIG, "1");
                this.put(ProducerConfig.BUFFER_MEMORY_CONFIG, "67108864");
            } catch (Throwable ex) {
                throw new RuntimeException(ex);
            }
        }
    };
}
