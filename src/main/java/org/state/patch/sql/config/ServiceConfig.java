package org.state.patch.sql.config;

public class ServiceConfig {

    public String                    name   = "state-patch-sql";
    public final ModelConfig         model  = new ModelConfig();
    public final EntityConfig        entity = new EntityConfig();
    public final TopicConsumerConfig patch  = new TopicConsumerConfig();
    public final TopicProducerConfig notify = new TopicProducerConfig();
}
