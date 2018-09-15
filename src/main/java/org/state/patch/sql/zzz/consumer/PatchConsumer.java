package org.state.patch.sql.zzz.consumer;

import java.util.function.Consumer;

import org.state.patch.sql.config.PatchTopicConfig;
import org.state.patch.sql.zzz.patch.Patch;

public interface PatchConsumer {

    public static PatchConsumer createConsumer(PatchTopicConfig config) {
        if (KafkaPatchConsumer.NAME.equals(config.engine)) {
            return new KafkaPatchConsumer(config);
        }
        throw new RuntimeException("Unkown patch topic engine: " + config.engine);
    }

    public void run(Consumer<Patch> processor);

}
