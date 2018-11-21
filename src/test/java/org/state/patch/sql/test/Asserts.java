package org.state.patch.sql.test;

import static org.junit.Assert.assertEquals;

import org.service.common.translator.JsonTranslator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface Asserts {

    public static void asserJson(String expectedJson, String actualJson) throws Exception {
        final ObjectMapper mapper = new ObjectMapper();

        final JsonNode expectedTree = mapper.readTree(expectedJson);
        final JsonNode actualTree = mapper.readTree(actualJson);

        assertEquals(expectedTree, actualTree);
    }

    public static <J> void asserJsonRoundtrip(ObjectMapper mapper, String jsonString, Class<J> jsonClass) throws Exception {
        // Setup
        J jsonInstance = mapper.readValue(jsonString, jsonClass);

        // Execute
        String jsonRoundtripString = mapper.writeValueAsString(jsonInstance);

        //Validate
        Asserts.asserJson(jsonString, jsonRoundtripString);
    }

    public static <E, J> void asserJsonTranslatorRoundtrip(JsonTranslator<E, J> translator,
                                                           ObjectMapper mapper,
                                                           String jsonString) throws Exception {
        // Setup
        J jsonInstance = mapper.readValue(jsonString, translator.getJsonClass());
        E entity = translator.fromJson(jsonInstance);

        // Execute
        J jsonRoundtripInstance = translator.toJson(entity);
        String jsonRoundtripString = mapper.writeValueAsString(jsonRoundtripInstance);

        //Validate
        Asserts.asserJson(jsonString, jsonRoundtripString);
    }
}
