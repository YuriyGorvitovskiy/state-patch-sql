package org.state.patch.sql.message.kafka;

import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.state.patch.sql.config.Configurator;
import org.state.patch.sql.config.MessageConsumerConfig;
import org.state.patch.sql.config.MessageProducerConfig;
import org.state.patch.sql.message.JsonMessage;
import org.state.patch.sql.translator.JsonTranslator;

public class Kafka_FunctionalTest {

    public static class Message {
        boolean              booleanValue;
        double               doubleValue;
        String               stringValue;
        List<Double>         listOfDouble;
        Map<String, Boolean> mapOfBoolean;
    }

    public static class Json extends JsonMessage {
        boolean              boolean_value;
        double               double_value;
        String               string_value;
        List<Double>         list_of_double;
        Map<String, Boolean> map_of_boolean;
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
            Message messaage = new Message();
            messaage.booleanValue = json.boolean_value;
            messaage.doubleValue = json.double_value;
            messaage.stringValue = json.string_value;
            messaage.listOfDouble = json.list_of_double;
            messaage.mapOfBoolean = json.map_of_boolean;
            return messaage;
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
        // Send & Receive Message
        fail("required implementation");

    }

}
