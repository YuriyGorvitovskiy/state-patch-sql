package org.state.patch.sql.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class Attribute_UnitTest {

    @Test
    public void constructor_toString() {
        // Setup
        final String name = "Name";
        final ValueType type = PrimitiveType.INTEGER;
        final Object initial = 123L;

        // Execute
        Attribute subject = new Attribute(name, type, initial);
        String string = subject.toString();

        // Validate
        assertSame(name, subject.name);
        assertSame(type, subject.type);
        assertSame(initial, subject.initial);
        assertEquals("Attribute (" + name + ")", string);
    }
}
