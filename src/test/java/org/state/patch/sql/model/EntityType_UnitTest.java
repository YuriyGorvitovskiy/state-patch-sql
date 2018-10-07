package org.state.patch.sql.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class EntityType_UnitTest {

    @Test
    public void constructor_toString() {
        // Setup
        final String type = "Entity";
        final Attribute identity = new Attribute("id", new ReferenceType(type, PrimitiveType.INTEGER), null);
        final Attribute attrName = new Attribute("name", PrimitiveType.STRING, "");
        final Attribute attrCheck = new Attribute("check", PrimitiveType.BOOLEAN, true);
        final Attribute attrComment = new Attribute("comment", PrimitiveType.TEXT, "");

        final List<Attribute> attrs = Arrays.asList(attrName, attrCheck, attrComment);

        // Execute
        EntityType subject = new EntityType(type, identity, attrs);
        String string = subject.toString();

        // Validate
        assertSame(type, subject.name);
        assertSame(identity, subject.identity);

        assertSame(identity, subject.attrs.get("id"));
        assertSame(attrName, subject.attrs.get("name"));
        assertSame(attrCheck, subject.attrs.get("check"));
        assertSame(attrComment, subject.attrs.get("comment"));
        assertSame(4, subject.attrs.size());

        assertEquals("EntityType (" + type + ")", string);
    }
}
