package org.state.patch.sql.config;

import org.service.common.message.MessageConsumerConfig;
import org.service.common.message.MessageProducerConfig;

public class ServiceConfig {

    public String                    name   = "state-patch-sql";
    public final ModelConfig         model  = new ModelConfig();
    public final EntityConfig        entity = new EntityConfig();
    public final MessageConsumerConfig patch  = new MessageConsumerConfig();
    public final MessageProducerConfig notify = new MessageProducerConfig();
}
