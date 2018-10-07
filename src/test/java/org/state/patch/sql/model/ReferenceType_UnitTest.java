package org.state.patch.sql.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class ReferenceType_UnitTest {

    @Test
    public void constructor() {
        // Setup
        final String entityType = "Entity";
        final PrimitiveType storageType = PrimitiveType.INTEGER;

        // Execute
        ReferenceType subject = new ReferenceType(entityType, storageType);

        // Validate
        assertSame(entityType, subject.entityType);
        assertSame(storageType, subject.storageType);
    }

    @Test
    public void equality() {
        // Setup
        ReferenceType subject1 = new ReferenceType("Entity", PrimitiveType.INTEGER);
        ReferenceType subject2 = new ReferenceType("Entity", PrimitiveType.INTEGER);
        ReferenceType subject3 = new ReferenceType("Entity", PrimitiveType.STRING);
        ReferenceType subject4 = new ReferenceType("Other", PrimitiveType.INTEGER);

        // Execute &* Validate
        assertEquals(subject1, subject2);
        assertNotEquals(subject1, subject3);
        assertNotEquals(subject1, subject4);
        assertNotEquals(subject1, null);
    }
}
