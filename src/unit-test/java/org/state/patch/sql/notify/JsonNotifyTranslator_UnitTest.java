package org.state.patch.sql.notify;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.state.patch.sql.Asserts;
import org.state.patch.sql.util.Json;
import org.state.patch.sql.util.ResourceString;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonNotifyTranslator_UnitTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    final ObjectMapper         mapper  = new ObjectMapper();
    final JsonNotifyTranslator subject = new JsonNotifyTranslator();

    @Test
    public void description() {
        // Execute & Validate
        assertEquals(Notify.class, subject.getEntityClass());
        assertEquals(JsonNotify.class, subject.getJsonClass());
    }

    @Test
    public void from_json_notify_v1() throws Exception {
        // Setup
        ResourceString resource = new ResourceString(JsonNotify_UnitTest.class, "JsonNotifyTranslator_UnitTest.notify.v1.json");
        JsonNotify jsonNotify = mapper.readValue(resource.toString(), JsonNotify.class);

        // Execute
        Notify notify = subject.fromJson(jsonNotify);

        //Validate
        assertNotNull(notify);
        assertEquals("service1", notify.processedBy);
        assertEquals(Json.DATE_FORMAT.parse("2018-09-30T22:19:47.123Z"), notify.processedAt);
        assertEquals(1L, notify.eventId);
        assertEquals(2L, notify.patchId);
    }

    @Test
    public void from_json_null() throws Exception {
        // Setup
        exception.expect(Exception.class);
        exception.expectMessage("Unknown notify: null");

        // Execute
        subject.fromJson((JsonNotify) null);
    }

    @Test
    public void to_json_notify_v1() throws Exception {
        // Setup
        ResourceString resource = new ResourceString(JsonNotify_UnitTest.class, "JsonNotifyTranslator_UnitTest.notify.v1.json");

        // Execute & Validate
        Asserts.asserJsonTranslatorRoundtrip(subject, mapper, resource.toString());
    }
}
