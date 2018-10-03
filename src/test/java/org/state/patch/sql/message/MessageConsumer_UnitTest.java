package org.state.patch.sql.message;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.state.patch.sql.config.MessageConsumerConfig;
import org.state.patch.sql.message.kafka.KafkaMessageConsumer;
import org.state.patch.sql.message.kafka.KafkaMessageProducer;
import org.state.patch.sql.translator.JsonTranslator;

public class MessageConsumer_UnitTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void create_kafka() {
        // Setup
        MessageConsumerConfig config = new MessageConsumerConfig();
        config.engine = KafkaMessageProducer.NAME;

        @SuppressWarnings("unchecked")
        JsonTranslator<String, JsonMessage> translator = Mockito.mock(JsonTranslator.class);

        //Execute
        MessageConsumer<String, JsonMessage> subject = MessageConsumer.create(config, translator);

        // Validate
        assertNotNull(subject);
        KafkaMessageConsumer<String, JsonMessage> kafka = (KafkaMessageConsumer<String, JsonMessage>) subject;
        assertSame(config, kafka.config);
        assertSame(translator, kafka.translator);
        assertNotNull(kafka.mapper);
        assertNull(kafka.messageClass);
    }

    @Test
    public void create_unknown() {
        // Setup
        MessageConsumerConfig config = new MessageConsumerConfig();
        config.engine = "unknown";

        @SuppressWarnings("unchecked")
        JsonTranslator<String, JsonMessage> translator = Mockito.mock(JsonTranslator.class);

        exception.expect(RuntimeException.class);
        exception.expectMessage("Unkown message consumer engine: unknown");

        //Execute
        MessageConsumer.create(config, translator);
    }
}
