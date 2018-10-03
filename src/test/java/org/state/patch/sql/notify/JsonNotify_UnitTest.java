package org.state.patch.sql.notify;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.state.patch.sql.notify.v1.JsonNotify_v1;
import org.state.patch.sql.test.Asserts;
import org.state.patch.sql.util.Json;
import org.state.patch.sql.util.ResourceString;

import com.fasterxml.jackson.databind.ObjectMapper;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JsonNotify_UnitTest {

    final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void read_notify_v1() throws Exception {
        // Setup
        ResourceString resource = new ResourceString(JsonNotify_UnitTest.class, "JsonNotify_UnitTest.notify.v1.json");

        // Execute
        JsonNotify notify = mapper.readValue(resource.toString(), JsonNotify.class);

        //Validate
        assertNotNull(notify);
        JsonNotify_v1 notify_v1 = (JsonNotify_v1) notify;

        // Validate JsonMessage
        assertEquals(0L, notify_v1.message_id);

        // Validate JsonNotify_v1
        assertEquals("service1", notify_v1.processed_by);
        assertEquals(Json.DATE_PARSE.parse("2018-09-30T22:19:47.123Z"), notify_v1.processed_at);
        assertEquals(1L, notify_v1.event_id);
        assertEquals(2L, notify_v1.patch_id);
    }

    @Test
    public void write_notify_v1() throws Exception {
        // Setup
        ResourceString resource = new ResourceString(JsonNotify_UnitTest.class, "JsonNotify_UnitTest.notify.v1.json");

        // Execute & Validate
        Asserts.asserJsonRoundtrip(mapper, resource.toString(), JsonNotify.class);
    }
}
