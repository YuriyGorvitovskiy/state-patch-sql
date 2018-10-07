package org.state.patch.sql.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PrimitiveType_UnitTest {

    @Test
    public void validateEnum() {
        // Execute & Validate
        assertEquals(7, PrimitiveType.values().length);
    }
}
