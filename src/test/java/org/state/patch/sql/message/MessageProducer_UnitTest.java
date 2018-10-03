package org.state.patch.sql.message;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.state.patch.sql.config.MessageProducerConfig;
import org.state.patch.sql.message.kafka.KafkaMessageProducer;
import org.state.patch.sql.translator.JsonTranslator;

public class MessageProducer_UnitTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void create_kafka() {
        // Setup
        MessageProducerConfig config = new MessageProducerConfig();
        config.engine = KafkaMessageProducer.NAME;

        @SuppressWarnings("unchecked")
        JsonTranslator<String, JsonMessage> translator = Mockito.mock(JsonTranslator.class);

        //Execute
        MessageProducer<String, JsonMessage> subject = MessageProducer.create(config, translator);

        // Validate
        assertNotNull(subject);
        KafkaMessageProducer<String, JsonMessage> kafka = (KafkaMessageProducer<String, JsonMessage>) subject;
        assertSame(config, kafka.config);
        assertSame(translator, kafka.translator);
        assertNotNull(kafka.mapper);
        assertNotNull(kafka.producer);
    }

    @Test
    public void create_unknown() {
        // Setup
        MessageProducerConfig config = new MessageProducerConfig();
        config.engine = "unknown";

        @SuppressWarnings("unchecked")
        JsonTranslator<String, JsonMessage> translator = Mockito.mock(JsonTranslator.class);

        exception.expect(RuntimeException.class);
        exception.expectMessage("Unkown message producer engine: unknown");

        //Execute
        MessageProducer.create(config, translator);
    }
}
