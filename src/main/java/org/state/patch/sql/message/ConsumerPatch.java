package org.state.patch.sql.message;

import java.util.function.Consumer;

import org.state.patch.sql.config.PatchTopicConfig;
import org.state.patch.sql.message.kafka.KafkaConsumerPatch;
import org.state.patch.sql.zzz.patch.Patch;

public interface ConsumerPatch {

    public static ConsumerPatch create(PatchTopicConfig config) {
        if (KafkaConsumerPatch.NAME.equals(config.engine)) {
            return new KafkaConsumerPatch(config);
        }
        throw new RuntimeException("Unkown patch topic engine: " + config.engine);
    }

    public void run(Consumer<Patch> processor);

}
