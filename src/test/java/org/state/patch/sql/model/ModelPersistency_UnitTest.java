package org.state.patch.sql.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.state.patch.sql.config.ModelConfig;
import org.state.patch.sql.data.ReferenceExternal;
import org.state.patch.sql.data.ReferenceInteger;
import org.state.patch.sql.data.ReferenceString;
import org.state.patch.sql.data.op.DataOpUpdate;
import org.state.patch.sql.db.Database;
import org.state.patch.sql.db.mock.MockDatabase;
import org.state.patch.sql.model.op.ModelOp;
import org.state.patch.sql.model.op.ModelOpAppendAttribute;
import org.state.patch.sql.model.op.ModelOpCreateType;
import org.state.patch.sql.model.op.ModelOpDeleteAttribute;
import org.state.patch.sql.model.op.ModelOpDeleteType;
import org.state.patch.sql.patch.JsonPatchTranslator;

public class ModelPersistency_UnitTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void constructor_config() {
        // Setup
        ModelConfig config = new ModelConfig();

        // Execute
        ModelPersistency subject = new ModelPersistency(config);

        // Verify
        assertNotNull(subject.modelModel);
        assertNotNull(subject.modelDatabase);
        assertNotNull(subject.jsonTranslator);
    }

    @Test
    public void constructor_explicit() {
        // Setup
        Model model = Mockito.mock(Model.class);
        Database database = Mockito.mock(Database.class);
        JsonPatchTranslator translator = Mockito.mock(JsonPatchTranslator.class);

        // Execute
        ModelPersistency subject = new ModelPersistency(model, database, translator);

        // Verify
        assertSame(model, subject.modelModel);
        assertSame(database, subject.modelDatabase);
        assertSame(translator, subject.jsonTranslator);
    }

    @Test
    public void initialize_empty() throws Exception {
        // Setup
        Model model = new Model();
        MockDatabase database = new MockDatabase();
        JsonPatchTranslator translator = new JsonPatchTranslator(model);

        ModelPersistency subject = new ModelPersistency(model, database, translator);

        // Execute
        subject.initialize();

        // Validate
        assertEquals(2, database.model.types.size());
        assertEquals(1, database.data.size());
    }

    @Test
    public void initialize_existing() throws Exception {
        // Setup
        Model model = new Model();
        MockDatabase database = new MockDatabase();
        JsonPatchTranslator translator = new JsonPatchTranslator(model);

        ModelPersistency original = new ModelPersistency(model, database, translator);
        original.initialize();

        model = new Model();
        translator = new JsonPatchTranslator(model);

        // Keep mock database the same
        ModelPersistency subject = new ModelPersistency(model, database, translator);

        // Execute
        subject.initialize();

        // Validate
        assertEquals(2, database.model.types.size());
        assertEquals(1, database.data.size());
    }

    @Test
    public void initialize_wrongVersion() throws Exception {
        // Setup
        Model model = new Model();
        MockDatabase database = new MockDatabase();
        JsonPatchTranslator translator = new JsonPatchTranslator(model);

        ModelPersistency original = new ModelPersistency(model, database, translator);
        original.initialize();

        model = new Model();
        translator = new JsonPatchTranslator(model);

        // Keep mock database the same
        ModelPersistency subject = new ModelPersistency(model, database, translator);

        // Alter version entity
        Map<String, Object> attrs = new HashMap<>();
        attrs.put(ModelPersistency.VersionAttr.TAG, "unknown");
        DataOpUpdate op = new DataOpUpdate(ModelPersistency.Ref.MODEL_VERSION, attrs);
        database.update(op);

        exception.expect(RuntimeException.class);
        exception.expectMessage("Unknown model version: unknown");

        // Execute
        subject.initialize();
    }

    @Test
    public void createType() throws Exception {
        // Setup
        ModelPersistency subject = createSubject();

        final String type = "Entity";
        final ModelOp.Attribute identity = new ModelOp.Attribute("id", new ReferenceType(type, PrimitiveType.INTEGER), null);
        final ModelOp.Attribute attrName = new ModelOp.Attribute("name", PrimitiveType.STRING, "");
        final ModelOp.Attribute attrCheck = new ModelOp.Attribute("check", PrimitiveType.BOOLEAN, true);
        final ModelOp.Attribute attrComment = new ModelOp.Attribute("comment", PrimitiveType.TEXT, "");

        final ModelOpCreateType op = new ModelOpCreateType(type, identity, Arrays.asList(attrName, attrCheck, attrComment));

        // Execute
        subject.createType(op);

        // Verify
        Model actualModel = new Model();
        subject.load(actualModel);

        EntityType entityType = actualModel.getEntityType(type);
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
    public void deleteType() throws Exception {
        // Setup
        ModelPersistency subject = createSubject();

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
        Model model = new Model();
        subject.load(model);

        EntityType entityType = model.getEntityType(type);
        assertNull(entityType);
    }

    @Test
    public void appendAttribute() throws Exception {
        // Setup
        ModelPersistency subject = createSubject();

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
        Model model = new Model();
        subject.load(model);

        EntityType entityType = model.getEntityType(type);
        assertNotNull(entityType);

        assertAttribute(attrAnother, entityType.attrs.get("another"));
    }

    @Test
    public void deleteAttribute() throws Exception {
        // Setup
        ModelPersistency subject = createSubject();

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
        Model model = new Model();
        subject.load(model);

        EntityType entityType = model.getEntityType(type);
        assertNotNull(entityType);

        assertAttribute(identity, entityType.attrs.get("id"));
        assertAttribute(attrName, entityType.attrs.get("name"));
        assertAttribute(attrCheck, entityType.attrs.get("check"));
        assertSame(3, entityType.attrs.size());

        Attribute attribute = entityType.attrs.get(attrComment.name);
        assertNull(attribute);
    }

    @Test
    public void toStringValue_valueType_null() {
        // Setup
        ModelPersistency subject = new ModelPersistency(null, null, null);

        exception.expect(RuntimeException.class);
        exception.expectMessage("Unsupported Value Type: null");

        // Execute
        subject.toStringValue(null);
    }

    @Test
    public void toValueType_unknown() {
        // Setup
        ModelPersistency subject = new ModelPersistency(null, null, null);

        exception.expect(RuntimeException.class);
        exception.expectMessage("Unsupported Value Type: unknown");

        // Execute
        subject.toValueType("unknown");
    }

    @Test
    public void value_roundtrip() throws Exception {
        // Setup
        ModelPersistency subject = new ModelPersistency(null, null, null);

        // Execute & Validate
        assertValueRoundTrip(subject,
                             PrimitiveType.BOOLEAN,
                             null);
        assertValueRoundTrip(subject,
                             PrimitiveType.BOOLEAN,
                             true);
        assertValueRoundTrip(subject,
                             PrimitiveType.INTEGER,
                             12345L);
        assertValueRoundTrip(subject,
                             PrimitiveType.DOUBLE,
                             123.45);
        assertValueRoundTrip(subject,
                             PrimitiveType.STRING,
                             "Hello");
        assertValueRoundTrip(subject,
                             PrimitiveType.TEXT,
                             "Hello World!");
        assertValueRoundTrip(subject,
                             PrimitiveType.TIMESTAMP,
                             new Date(1234567890123L));
        assertValueRoundTrip(subject,
                             PrimitiveType.REFERENCE_EXTERNAL,
                             new ReferenceExternal("Entity:EXT-12345678"));
        assertValueRoundTrip(subject,
                             new ReferenceType("Entity", PrimitiveType.STRING),
                             new ReferenceString("Entity", "123-45-678"));
        assertValueRoundTrip(subject,
                             new ReferenceType("Entity", PrimitiveType.INTEGER),
                             new ReferenceInteger("Entity", 12345678L));
    }

    @Test
    public void toStringValue_value_valueType_null() {
        // Setup
        ModelPersistency subject = new ModelPersistency(null, null, null);

        exception.expect(RuntimeException.class);
        exception.expectMessage("Unsupported Value Type: null");

        // Execute
        subject.toStringValue(null, "some value");
    }

    @Test
    public void toValue_valueType_null() throws Exception {
        // Setup
        ModelPersistency subject = new ModelPersistency(null, null, null);

        exception.expect(RuntimeException.class);
        exception.expectMessage("Unsupported Value Type: null");

        // Execute
        subject.toValue(null, "some value");
    }

    ModelPersistency createSubject() throws Exception {
        Model model = new Model();
        MockDatabase database = new MockDatabase();
        JsonPatchTranslator translator = new JsonPatchTranslator(model);

        ModelPersistency subject = new ModelPersistency(model, database, translator);
        subject.initialize();

        return subject;
    }

    void assertAttribute(ModelOp.Attribute op, Attribute attr) {
        assertEquals(op.name, attr.name);
        assertEquals(op.type, attr.type);
        assertEquals(op.initial, attr.initial);
    }

    void assertValueRoundTrip(ModelPersistency subject, ValueType type, Object value) throws Exception {
        String string = subject.toStringValue(type, value);
        Object restored = subject.toValue(type, string);

        // Verify
        if (null != value && null != restored) {
            assertEquals(value.getClass(), restored.getClass());
        }
        assertEquals(value, restored);
    }

}
