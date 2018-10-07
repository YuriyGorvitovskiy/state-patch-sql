package org.state.patch.sql.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.Date;

import org.junit.Test;
import org.state.patch.sql.model.op.ModelOp;
import org.state.patch.sql.model.op.ModelOpAppendAttribute;
import org.state.patch.sql.model.op.ModelOpCreateType;
import org.state.patch.sql.model.op.ModelOpDeleteAttribute;
import org.state.patch.sql.model.op.ModelOpDeleteType;

public class Model_UnitTest {

    Model subject = new Model();

    @Test
    public void validate_toString() {
        // Execute & Validate
        assertEquals("Model", subject.toString());
    }

    @Test
    public void createType() {
        // Setup
        final String type = "Entity";
        final ModelOp.Attribute identity = new ModelOp.Attribute("id", new ReferenceType(type, PrimitiveType.INTEGER), null);
        final ModelOp.Attribute attrName = new ModelOp.Attribute("name", PrimitiveType.STRING, "");
        final ModelOp.Attribute attrCheck = new ModelOp.Attribute("check", PrimitiveType.BOOLEAN, true);
        final ModelOp.Attribute attrComment = new ModelOp.Attribute("comment", PrimitiveType.TEXT, "");

        ModelOpCreateType op = new ModelOpCreateType(type, identity, Arrays.asList(attrName, attrCheck, attrComment));

        // Execute
        subject.createType(op);

        // Validate
        EntityType entityType = subject.getEntityType(type);
        assertNotNull(entityType);

        assertSame(type, entityType.name);

        assertAttribute(identity, entityType.identity);

        assertAttribute(identity, entityType.attrs.get("id"));
        assertAttribute(attrName, entityType.attrs.get("name"));
        assertAttribute(attrCheck, entityType.attrs.get("check"));
        assertAttribute(attrComment, entityType.attrs.get("comment"));
        assertSame(4, entityType.attrs.size());
    }

    @Test
    public void deleteType() {
        // Setup
        final String type = "Entity";
        final ModelOp.Attribute identity = new ModelOp.Attribute("id", new ReferenceType(type, PrimitiveType.INTEGER), null);
        final ModelOp.Attribute attrName = new ModelOp.Attribute("name", PrimitiveType.STRING, "");
        final ModelOp.Attribute attrCheck = new ModelOp.Attribute("check", PrimitiveType.BOOLEAN, true);
        final ModelOp.Attribute attrComment = new ModelOp.Attribute("comment", PrimitiveType.TEXT, "");

        ModelOpCreateType opCreate = new ModelOpCreateType(type, identity, Arrays.asList(attrName, attrCheck, attrComment));
        subject.createType(opCreate);

        ModelOpDeleteType opDelete = new ModelOpDeleteType(type);

        // Execute
        subject.deleteType(opDelete);

        // Validate
        EntityType entityType = subject.getEntityType(type);
        assertNull(entityType);
    }

    @Test
    public void appendAttribute() {
        // Setup
        final String type = "Entity";
        final ModelOp.Attribute identity = new ModelOp.Attribute("id", new ReferenceType(type, PrimitiveType.INTEGER), null);
        final ModelOp.Attribute attrName = new ModelOp.Attribute("name", PrimitiveType.STRING, "");
        final ModelOp.Attribute attrCheck = new ModelOp.Attribute("check", PrimitiveType.BOOLEAN, true);
        final ModelOp.Attribute attrComment = new ModelOp.Attribute("comment", PrimitiveType.TEXT, "");

        ModelOpCreateType opCreate = new ModelOpCreateType(type, identity, Arrays.asList(attrName, attrCheck, attrComment));
        subject.createType(opCreate);

        final ModelOp.Attribute attrAnother = new ModelOp.Attribute("another", PrimitiveType.TIMESTAMP, new Date(0L));
        ModelOpAppendAttribute opAppend = new ModelOpAppendAttribute(type, attrAnother);

        // Execute
        subject.appendAttribute(opAppend);

        // Validate
        EntityType entityType = subject.getEntityType(type);
        assertNotNull(entityType);

        assertAttribute(attrAnother, entityType.attrs.get("another"));
    }

    @Test
    public void deleteAttribute() {
        // Setup
        final String type = "Entity";
        final ModelOp.Attribute identity = new ModelOp.Attribute("id", new ReferenceType(type, PrimitiveType.INTEGER), null);
        final ModelOp.Attribute attrName = new ModelOp.Attribute("name", PrimitiveType.STRING, "");
        final ModelOp.Attribute attrCheck = new ModelOp.Attribute("check", PrimitiveType.BOOLEAN, true);
        final ModelOp.Attribute attrComment = new ModelOp.Attribute("comment", PrimitiveType.TEXT, "");

        ModelOpCreateType opCreate = new ModelOpCreateType(type, identity, Arrays.asList(attrName, attrCheck, attrComment));
        subject.createType(opCreate);

        ModelOpDeleteAttribute opDelete = new ModelOpDeleteAttribute(type, attrComment.name);

        // Execute
        subject.deleteAttribute(opDelete);

        // Validate
        EntityType entityType = subject.getEntityType(type);
        assertNotNull(entityType);

        assertAttribute(identity, entityType.attrs.get("id"));
        assertAttribute(attrName, entityType.attrs.get("name"));
        assertAttribute(attrCheck, entityType.attrs.get("check"));
        assertSame(3, entityType.attrs.size());

        Attribute attribute = entityType.attrs.get(attrComment.name);
        assertNull(attribute);
    }

    void assertAttribute(ModelOp.Attribute op, Attribute attr) {
        assertSame(op.name, attr.name);
        assertSame(op.type, attr.type);
        assertSame(op.initial, attr.initial);
    }
}
