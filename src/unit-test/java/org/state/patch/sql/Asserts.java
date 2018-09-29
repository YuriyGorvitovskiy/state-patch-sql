package org.state.patch.sql;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Asserts {

    public static void asserJson(String expectedJson, String actualJson) throws Exception {
        final ObjectMapper mapper = new ObjectMapper();

        final JsonNode expectedTree = mapper.readTree(expectedJson);
        final JsonNode actualTree = mapper.readTree(actualJson);

        assertEquals(expectedTree, actualTree);
    }

    public static <T> void asserJsonRoundtrip(ObjectMapper mapper, String jsonString, Class<T> jsonClass) throws Exception {
        // Setup
        T jsonInstance = mapper.readValue(jsonString, jsonClass);

        // Execute
        String jsonRoundtripString = mapper.writeValueAsString(jsonInstance);

        //Validate
        Asserts.asserJson(jsonString, jsonRoundtripString);
    }
}
