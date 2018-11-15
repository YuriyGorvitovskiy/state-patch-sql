package org.state.patch.sql.message.kafka;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.service.common.config.Configurator;
import org.state.patch.sql.config.MessageConsumerConfig;
import org.state.patch.sql.config.MessageProducerConfig;
import org.state.patch.sql.message.JsonMessage;
import org.state.patch.sql.translator.JsonTranslator;

public class Kafka_FunctionalTest {

    public static class Message {
        long                 id;
        boolean              booleanValue;
        double               doubleValue;
        String               stringValue;
        List<Double>         listOfDouble;
        Map<String, Boolean> mapOfBoolean;
    }

    public static class Json extends JsonMessage {
        public boolean              boolean_value;
        public double               double_value;
        public String               string_value;
        public List<Double>         list_of_double;
        public Map<String, Boolean> map_of_boolean;
    }

    public static class Transmuter implements JsonTranslator<Message, Json> {

        @Override
        public Class<Message> getEntityClass() {
            return Message.class;
        }

        @Override
        public Class<Json> getJsonClass() {
            return Json.class;
        }

        @Override
        public Json toJson(Message messaage) throws Exception {
            Json json = new Json();
            json.boolean_value = messaage.booleanValue;
            json.double_value = messaage.doubleValue;
            json.string_value = messaage.stringValue;
            json.list_of_double = messaage.listOfDouble;
            json.map_of_boolean = messaage.mapOfBoolean;
            return json;
        }

        @Override
        public Message fromJson(Json json) throws Exception {
            Message message = new Message();
            message.id = json.message_id;
            message.booleanValue = json.boolean_value;
            message.doubleValue = json.double_value;
            message.stringValue = json.string_value;
            message.listOfDouble = json.list_of_double;
            message.mapOfBoolean = json.map_of_boolean;
            return message;
        }

    }

    KafkaMessageProducer<Message, Json> producer;
    KafkaMessageConsumer<Message, Json> consumer;

    @Before
    public void setup() {
        MessageProducerConfig producerConfig = Configurator.extract(System.getProperties(),
                                                                    "test.org.state.patch.sql.message.kafka.producer",
                                                                    new MessageProducerConfig());

        MessageConsumerConfig consumerConfig = Configurator.extract(System.getProperties(),
                                                                    "test.org.state.patch.sql.message.kafka.consumer",
                                                                    new MessageConsumerConfig());

        producer = new KafkaMessageProducer<>(producerConfig, new Transmuter());
        consumer = new KafkaMessageConsumer<>(consumerConfig, new Transmuter());
    }

    @Test
    public void process() throws Exception {
        // Send Messages
        Message msg1 = new Message();
        msg1.booleanValue = true;
        msg1.doubleValue = 1.23;
        msg1.stringValue = "Hello";
        msg1.listOfDouble = Arrays.asList(2.34, 3.45);
        msg1.mapOfBoolean = new HashMap<>();
        msg1.mapOfBoolean.put("true", true);
        msg1.mapOfBoolean.put("false", false);

        Message msg2 = new Message();
        Message msg3 = new Message();
        msg3.booleanValue = false;
        msg3.doubleValue = 6.78;
        msg3.stringValue = "World";
        msg3.listOfDouble = Arrays.asList(7.89, 8.90);
        msg3.mapOfBoolean = new HashMap<>();
        msg3.mapOfBoolean.put("positive", true);
        msg3.mapOfBoolean.put("negative", false);

        producer.post(msg1);
        msg1.id = producer.getLastOffset();

        producer.post(msg2);
        msg2.id = producer.getLastOffset();

        producer.post(msg3);
        msg3.id = producer.getLastOffset();

        // Receive Messages
        List<Message> recieved = new ArrayList<>();
        String interrupt = "Iterrupt";
        try {
            consumer.run(msg1.id, (msg) -> {
                recieved.add(msg);
                if (3 == recieved.size()) {
                    throw new RuntimeException(interrupt);
                }
            });
        } catch (Throwable ex) {
            if (!interrupt.equals(ex.getCause().getMessage())) {
                throw ex;
            }
        }

        // Validate
        assertMessage(msg1, recieved.get(0));
        assertMessage(msg2, recieved.get(1));
        assertMessage(msg3, recieved.get(2));
    }

    void assertMessage(Message sent, Message received) {
        assertEquals(sent.id, received.id);
        assertEquals(sent.booleanValue, received.booleanValue);
        assertEquals(sent.doubleValue, received.doubleValue, 0.0001);
        assertEquals(sent.stringValue, received.stringValue);
        assertEquals(sent.listOfDouble, received.listOfDouble);
        assertEquals(sent.mapOfBoolean, received.mapOfBoolean);
    }

}
