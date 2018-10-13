package org.state.patch.sql.util;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class Json_UnitTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void parseDate() {
        // Execute
        Date date = Json.parseDate("2018-10-12T19:39:13.123Z");

        // Verify
        assertEquals(new Date(1539373153123L).getTime(), date.getTime());
    }

    @Test
    public void parseDate_failed() {
        // Rule
        exception.expect(RuntimeException.class);
        exception.expectMessage("garbage");

        // Execute
        Json.parseDate("garbage");
    }

    @Test
    public void formatDate() {
        // Execute
        String str = Json.formatDate(new Date(1539373153123L));

        // Verify
        assertEquals("2018-10-12T19:39:13.123Z", str);
    }
}
