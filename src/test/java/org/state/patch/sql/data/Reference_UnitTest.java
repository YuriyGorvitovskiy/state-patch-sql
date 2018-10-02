package org.state.patch.sql.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class Reference_UnitTest {

    @Test
    public void constructor_toString() {
        // Setup
        final String refString = "reference:123-234";

        // Execute
        final Reference subject = new Reference(refString) {
        };

        // Setup
        assertSame(refString, subject.stringValue);
        assertSame(refString, subject.toString());
    }

    @Test
    public void equals_hashValue() {
        // Setup
        final String refString1 = "reference:123-234";
        final String refString2 = "reference:123-234";
        final String refString3 = "reference:234-567";

        // Execute
        final Reference subject1 = new Reference(refString1) {
        };
        final Reference subject2 = new Reference(refString2) {
        };
        final Reference subject3 = new Reference(refString3) {
        };

        // Setup
        assertEquals(subject1, subject2);
        assertNotEquals(subject1, subject3);
        assertNotEquals(subject2, subject3);
        assertNotEquals(subject2, null);

        assertEquals(subject1.hashCode(), subject2.hashCode());
        assertNotEquals(subject1.hashCode(), subject3.hashCode());
        assertNotEquals(subject2.hashCode(), subject3.hashCode());
    }
}
