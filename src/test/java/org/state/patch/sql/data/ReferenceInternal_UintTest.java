package org.state.patch.sql.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.state.patch.sql.model.PrimitiveType;
import org.state.patch.sql.model.ReferenceType;

public class ReferenceInternal_UintTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void constructor() {
        // Setup
        final String type = "reference";
        final String refString = "reference:123-234";

        // Execute
        final ReferenceInternal subject = new ReferenceInternal(type, refString) {
        };

        // Setup
        assertSame(type, subject.type);
        assertSame(refString, subject.stringValue);
    }

    @Test
    public void referenceFromString() {
        // Setup
        final ReferenceType integerType = new ReferenceType("typeA", PrimitiveType.INTEGER);
        final ReferenceType stringType = new ReferenceType("typeB", PrimitiveType.STRING);
        final String refInteger = "typeA:123";
        final String refString = "typeB:123-234";

        // Execute
        final ReferenceInternal ref1 = ReferenceInternal.referenceFromString(integerType, refInteger);
        final ReferenceInternal ref2 = ReferenceInternal.referenceFromString(stringType, refString);

        // Verify
        ReferenceInteger integerRef = (ReferenceInteger) ref1;
        assertEquals(123L, integerRef.id);
        assertEquals("typeA", integerRef.type);
        assertEquals("typeA:123", integerRef.stringValue);

        ReferenceString stringRef = (ReferenceString) ref2;
        assertEquals("123-234", stringRef.id);
        assertEquals("typeB", stringRef.type);
        assertEquals("typeB:123-234", stringRef.stringValue);
    }

    @Test
    public void referenceFromObject() {
        // Setup
        final ReferenceType integerType = new ReferenceType("typeA", PrimitiveType.INTEGER);
        final ReferenceType stringType = new ReferenceType("typeB", PrimitiveType.STRING);

        // Execute
        final ReferenceInternal ref1 = ReferenceInternal.referenceFromObject(integerType, 123);
        final ReferenceInternal ref2 = ReferenceInternal.referenceFromObject(integerType, "456");
        final ReferenceInternal ref3 = ReferenceInternal.referenceFromObject(stringType, "123-234");

        // Verify
        ReferenceInteger integerRef1 = (ReferenceInteger) ref1;
        assertEquals(123L, integerRef1.id);
        assertEquals("typeA", integerRef1.type);
        assertEquals("typeA:123", integerRef1.stringValue);

        ReferenceInteger integerRef2 = (ReferenceInteger) ref2;
        assertEquals(456L, integerRef2.id);
        assertEquals("typeA", integerRef2.type);
        assertEquals("typeA:456", integerRef2.stringValue);

        ReferenceString stringRef3 = (ReferenceString) ref3;
        assertEquals("123-234", stringRef3.id);
        assertEquals("typeB", stringRef3.type);
        assertEquals("typeB:123-234", stringRef3.stringValue);
    }

    @Test
    public void referenceFromObject_wrongType() {
        // Setup
        final ReferenceType wrongType = new ReferenceType("typeA", PrimitiveType.DOUBLE);

        exception.expect(RuntimeException.class);
        exception.expectMessage("Unknown reference storage type: DOUBLE");

        // Execute
        ReferenceInternal.referenceFromObject(wrongType, 123.23);
    }
}
