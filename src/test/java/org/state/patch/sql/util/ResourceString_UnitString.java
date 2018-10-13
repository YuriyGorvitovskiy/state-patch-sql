package org.state.patch.sql.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ResourceString_UnitString {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void toString_success() {
        // Setup
        ResourceString subject = new ResourceString(ResourceString_UnitString.class, "ResourceString_UnitString.filled.txt");

        // Execute
        String firstCall = subject.toString();
        String secondCall = subject.toString();

        // Verify
        assertEquals("Hello Resource String!", firstCall);
        assertSame(firstCall, secondCall);
    }

    @Test
    public void toString_empty() {
        // Setup
        ResourceString subject = new ResourceString(ResourceString_UnitString.class, "ResourceString_UnitString.empty.txt");

        // Execute
        String result = subject.toString();

        // Verify
        assertEquals("", result);
    }

    @Test
    public void toString_failure() {
        // Setup
        ResourceString subject = new ResourceString(ResourceString_UnitString.class, "not-existing.txt");

        // Rule
        exception.expect(RuntimeException.class);
        exception.expectMessage("resource 'not-existing.txt' related to "
                                + ResourceString_UnitString.class.getName()
                                + " class");

        // Execute
        subject.toString();
    }

}
