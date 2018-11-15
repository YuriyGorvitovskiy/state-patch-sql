package org.state.patch.sql.db.postgres;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.service.common.config.Configurator;
import org.state.patch.sql.config.DatabaseConfig;
import org.state.patch.sql.data.Entity;
import org.state.patch.sql.data.ReferenceExternal;
import org.state.patch.sql.data.ReferenceInteger;
import org.state.patch.sql.data.ReferenceString;
import org.state.patch.sql.data.op.DataOpDelete;
import org.state.patch.sql.data.op.DataOpInsert;
import org.state.patch.sql.data.op.DataOpUpdate;
import org.state.patch.sql.model.Attribute;
import org.state.patch.sql.model.EntityType;
import org.state.patch.sql.model.Model;
import org.state.patch.sql.model.PrimitiveType;
import org.state.patch.sql.model.ReferenceType;
import org.state.patch.sql.model.op.ModelOp;
import org.state.patch.sql.model.op.ModelOpAppendAttribute;
import org.state.patch.sql.model.op.ModelOpCreateType;
import org.state.patch.sql.model.op.ModelOpDeleteAttribute;
import org.state.patch.sql.model.op.ModelOpDeleteType;
import org.state.patch.sql.patch.PatchModel;

public class Postgres_FunctionalTest {

    final String ENTITY_TYPE = "test_entity_type";
    final String OTHER_TYPE  = "test_other_type";

    final String ATTR_IDENTITY  = "id";
    final String ATTR_BOOLEAN   = "attr_boolean";
    final String ATTR_INTEGER   = "attr_integer";
    final String ATTR_DOUBLE    = "attr_double";
    final String ATTR_STRING    = "attr_string";
    final String ATTR_TEXT      = "attr_text";
    final String ATTR_TIMESTAMP = "attr_timestamp";
    final String REF_INTEGER    = "ref_integer";
    final String REF_STRING     = "ref_string";
    final String REF_EXTERNAL   = "ref_external";

    final Boolean           DEFAULT_BOOLEAN      = Boolean.TRUE;
    final Long              DEFAULT_INTEGER      = 1L;
    final Double            DEFAULT_DOUBLE       = 2.3;
    final String            DEFAULT_STRING       = "Hello";
    final String            DEFAULT_TEXT         = "Hello 'Text'!";
    final Date              DEFAULT_TIMESTAMP    = new Date(1234567890L);
    final ReferenceInteger  DEFAULT_REF_INTEGER  = new ReferenceInteger(ENTITY_TYPE, 22);
    final ReferenceString   DEFAULT_REF_STRING   = new ReferenceString(OTHER_TYPE, "first");
    final ReferenceExternal DEFAULT_REF_EXTERNAL = new ReferenceExternal("http://example.com");

    final Boolean INST1_V1_BOOLEAN   = Boolean.FALSE;
    final Long    INST1_V1_INTEGER   = 2L;
    final Double  INST1_V1_DOUBLE    = 3.4;
    final String  INST1_V1_STRING    = "Hi";
    final String  INST1_V1_TEXT      = "Hi Text!";
    final Date    INST1_V1_TIMESTAMP = new Date(1234509876L);

    final Boolean           INST1_V2_BOOLEAN      = Boolean.TRUE;
    final Double            INST1_V2_DOUBLE       = 4.5;
    final String            INST1_V2_TEXT         = "Text V2!";
    final ReferenceInteger  INST1_V2_REF_INTEGER  = new ReferenceInteger(ENTITY_TYPE, 23);
    final ReferenceExternal INST1_V2_REF_EXTERNAL = new ReferenceExternal("http://example.com/v2");

    final Boolean         INST2_V2_BOOLEAN    = Boolean.FALSE;
    final Long            INST2_V2_INTEGER    = 6L;
    final String          INST2_V2_STRING     = "Welcome";
    final Date            INST2_V2_TIMESTAMP  = new Date(6789012345L);
    final ReferenceString INST2_V2_REF_STRING = new ReferenceString(OTHER_TYPE, "second");

    final ReferenceInteger INST1_ID = new ReferenceInteger(ENTITY_TYPE, 1);
    final ReferenceInteger INST2_ID = new ReferenceInteger(ENTITY_TYPE, 2);
    final ReferenceInteger INST3_ID = new ReferenceInteger(ENTITY_TYPE, 3);

    Model    model;
    Postgres subject;

    @Before
    public void setup() throws Exception {
        model = new Model();
        DatabaseConfig config = Configurator.extract(System.getProperties(),
                                                     "test.org.state.patch.sql.db.postgres",
                                                     new DatabaseConfig());

        subject = new Postgres(model, config);
        resetDatabase();
    }

    @After
    public void resetDatabase() throws Exception {
        if (subject.isTypeExists(ENTITY_TYPE)) {
            subject.deleteType(new ModelOpDeleteType(ENTITY_TYPE));
        }
    }

    @Test
    public void process() throws Exception {
        // Create Type with attributes only
        // Check that type exists
        createType();

        // Create instance 1 with all attribute (explicit)
        // Check that all attributes has correct value
        createInstance1();

        // Create instance 2 with no attribute (implicit)
        // Check that all attributes has correct value
        createInstance2();

        // Extend type with references
        // Check that all attributes has correct value
        appendReferencesToType();

        // Update instance 1 with first half attributes and references
        // Check that all attributes has correct value
        updateInstance1();

        // Update instance 2 with second half attributes and references
        // Check that all attributes has correct value
        updateInstance2();

        // Check select half attribute
        selectAttributesWithId();
        selectAttributesWithoutId();

        // Check select filter by every attribute
        selectWithAttributesCondition();

        // Check select sorting by every attribute
        selectWithAttributesSorting();

        // Remove all attributes and keep references
        // Check that all references has correct value
        removeAttributesFromType();

        // Delete instance 1
        // Check that instance 1 not exists and instance 2 exists
        deleteInstance1();

        // Delete type
        // Check that type not exists
        deleteType();
    }

    private void createType() throws Exception {
        // Setup
        ModelOp.Attribute identity = new ModelOp.Attribute(ATTR_IDENTITY,
                                                           new ReferenceType(ENTITY_TYPE, PrimitiveType.INTEGER),
                                                           null);

        List<ModelOp.Attribute> attributes = new ArrayList<>();
        attributes.add(new ModelOp.Attribute(ATTR_BOOLEAN, PrimitiveType.BOOLEAN, DEFAULT_BOOLEAN));
        attributes.add(new ModelOp.Attribute(ATTR_INTEGER, PrimitiveType.INTEGER, DEFAULT_INTEGER));
        attributes.add(new ModelOp.Attribute(ATTR_DOUBLE, PrimitiveType.DOUBLE, DEFAULT_DOUBLE));
        attributes.add(new ModelOp.Attribute(ATTR_STRING, PrimitiveType.STRING, DEFAULT_STRING));
        attributes.add(new ModelOp.Attribute(ATTR_TEXT, PrimitiveType.TEXT, DEFAULT_TEXT));
        attributes.add(new ModelOp.Attribute(ATTR_TIMESTAMP, PrimitiveType.TIMESTAMP, DEFAULT_TIMESTAMP));

        ModelOpCreateType op = new ModelOpCreateType(ENTITY_TYPE, identity, attributes);
        model.createType(op);

        // Execute
        subject.createType(op);

        // Validate
        assertTrue(subject.isTypeExists(ENTITY_TYPE));
    }

    private void createInstance1() throws Exception {
        // Setup
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(ATTR_BOOLEAN, INST1_V1_BOOLEAN);
        attributes.put(ATTR_INTEGER, INST1_V1_INTEGER);
        attributes.put(ATTR_DOUBLE, INST1_V1_DOUBLE);
        attributes.put(ATTR_STRING, INST1_V1_STRING);
        attributes.put(ATTR_TEXT, INST1_V1_TEXT);
        attributes.put(ATTR_TIMESTAMP, INST1_V1_TIMESTAMP);

        DataOpInsert op = new DataOpInsert(INST1_ID, attributes);

        // Execute
        subject.insert(op);

        // Validate
        EntityType type = model.getEntityType(ENTITY_TYPE);
        List<Attribute> selectAttributes = new ArrayList<>();
        selectAttributes.add(type.attrs.get(ATTR_IDENTITY));
        selectAttributes.add(type.attrs.get(ATTR_BOOLEAN));
        selectAttributes.add(type.attrs.get(ATTR_INTEGER));
        selectAttributes.add(type.attrs.get(ATTR_DOUBLE));
        selectAttributes.add(type.attrs.get(ATTR_STRING));
        selectAttributes.add(type.attrs.get(ATTR_TEXT));
        selectAttributes.add(type.attrs.get(ATTR_TIMESTAMP));

        Entity actual = subject.select(selectAttributes, INST1_ID);
        assertEquals(INST1_ID, actual.id);
        assertEquals(INST1_V1_BOOLEAN, actual.attrs.get(ATTR_BOOLEAN));
        assertEquals(INST1_V1_INTEGER, actual.attrs.get(ATTR_INTEGER));
        assertEquals(INST1_V1_DOUBLE, actual.attrs.get(ATTR_DOUBLE));
        assertEquals(INST1_V1_STRING, actual.attrs.get(ATTR_STRING));
        assertEquals(INST1_V1_TEXT, actual.attrs.get(ATTR_TEXT));
        assertEquals(INST1_V1_TIMESTAMP, actual.attrs.get(ATTR_TIMESTAMP));
    }

    private void createInstance2() throws Exception {
        // Setup
        Map<String, Object> attributes = new HashMap<>();
        DataOpInsert op = new DataOpInsert(INST2_ID, attributes);

        // Execute
        subject.insert(op);

        // Validate
        EntityType type = model.getEntityType(ENTITY_TYPE);
        List<Attribute> selectAttributes = new ArrayList<>();
        selectAttributes.add(type.attrs.get(ATTR_IDENTITY));
        selectAttributes.add(type.attrs.get(ATTR_BOOLEAN));
        selectAttributes.add(type.attrs.get(ATTR_INTEGER));
        selectAttributes.add(type.attrs.get(ATTR_DOUBLE));
        selectAttributes.add(type.attrs.get(ATTR_STRING));
        selectAttributes.add(type.attrs.get(ATTR_TEXT));
        selectAttributes.add(type.attrs.get(ATTR_TIMESTAMP));

        Entity actual = subject.select(selectAttributes, INST2_ID);
        assertEquals(INST2_ID, actual.id);
        assertEquals(DEFAULT_BOOLEAN, actual.attrs.get(ATTR_BOOLEAN));
        assertEquals(DEFAULT_INTEGER, actual.attrs.get(ATTR_INTEGER));
        assertEquals(DEFAULT_DOUBLE, actual.attrs.get(ATTR_DOUBLE));
        assertEquals(DEFAULT_STRING, actual.attrs.get(ATTR_STRING));
        assertEquals(DEFAULT_TEXT, actual.attrs.get(ATTR_TEXT));
        assertEquals(DEFAULT_TIMESTAMP, actual.attrs.get(ATTR_TIMESTAMP));
    }

    private void appendReferencesToType() throws Exception {
        // Setup
        List<ModelOp.Attribute> attributes = new ArrayList<>();
        attributes.add(new ModelOp.Attribute(ATTR_BOOLEAN, PrimitiveType.BOOLEAN, DEFAULT_BOOLEAN));
        attributes.add(new ModelOp.Attribute(ATTR_INTEGER, PrimitiveType.INTEGER, DEFAULT_INTEGER));
        attributes.add(new ModelOp.Attribute(ATTR_DOUBLE, PrimitiveType.DOUBLE, DEFAULT_DOUBLE));
        attributes.add(new ModelOp.Attribute(ATTR_STRING, PrimitiveType.STRING, DEFAULT_STRING));
        attributes.add(new ModelOp.Attribute(ATTR_TEXT, PrimitiveType.TEXT, DEFAULT_TEXT));
        attributes.add(new ModelOp.Attribute(ATTR_TIMESTAMP, PrimitiveType.TIMESTAMP, DEFAULT_TIMESTAMP));

        ModelOp.Attribute ref1 = new ModelOp.Attribute(REF_INTEGER,
                                                       new ReferenceType(ENTITY_TYPE, PrimitiveType.INTEGER),
                                                       DEFAULT_REF_INTEGER);
        ModelOp.Attribute ref2 = new ModelOp.Attribute(REF_STRING,
                                                       new ReferenceType(OTHER_TYPE, PrimitiveType.STRING),
                                                       DEFAULT_REF_STRING);
        ModelOp.Attribute ref3 = new ModelOp.Attribute(REF_EXTERNAL,
                                                       PrimitiveType.REFERENCE_EXTERNAL,
                                                       DEFAULT_REF_EXTERNAL);
        ModelOpAppendAttribute op1 = new ModelOpAppendAttribute(ENTITY_TYPE, ref1);
        ModelOpAppendAttribute op2 = new ModelOpAppendAttribute(ENTITY_TYPE, ref2);
        ModelOpAppendAttribute op3 = new ModelOpAppendAttribute(ENTITY_TYPE, ref3);
        model.appendAttribute(op1);
        model.appendAttribute(op2);
        model.appendAttribute(op3);

        // Execute
        subject.appendAttribute(op1);
        subject.appendAttribute(op2);
        subject.appendAttribute(op3);

        // Validate
        EntityType type = model.getEntityType(ENTITY_TYPE);
        List<Attribute> selectAttributes = new ArrayList<>();
        selectAttributes.add(type.attrs.get(ATTR_IDENTITY));
        selectAttributes.add(type.attrs.get(ATTR_BOOLEAN));
        selectAttributes.add(type.attrs.get(ATTR_INTEGER));
        selectAttributes.add(type.attrs.get(ATTR_DOUBLE));
        selectAttributes.add(type.attrs.get(ATTR_STRING));
        selectAttributes.add(type.attrs.get(ATTR_TEXT));
        selectAttributes.add(type.attrs.get(ATTR_TIMESTAMP));
        selectAttributes.add(type.attrs.get(REF_INTEGER));
        selectAttributes.add(type.attrs.get(REF_STRING));
        selectAttributes.add(type.attrs.get(REF_EXTERNAL));

        Entity actual1 = subject.select(selectAttributes, INST1_ID);
        assertEquals(INST1_ID, actual1.id);
        assertEquals(INST1_V1_BOOLEAN, actual1.attrs.get(ATTR_BOOLEAN));
        assertEquals(INST1_V1_INTEGER, actual1.attrs.get(ATTR_INTEGER));
        assertEquals(INST1_V1_DOUBLE, actual1.attrs.get(ATTR_DOUBLE));
        assertEquals(INST1_V1_STRING, actual1.attrs.get(ATTR_STRING));
        assertEquals(INST1_V1_TEXT, actual1.attrs.get(ATTR_TEXT));
        assertEquals(INST1_V1_TIMESTAMP, actual1.attrs.get(ATTR_TIMESTAMP));
        assertEquals(DEFAULT_REF_INTEGER, actual1.attrs.get(REF_INTEGER));
        assertEquals(DEFAULT_REF_STRING, actual1.attrs.get(REF_STRING));
        assertEquals(DEFAULT_REF_EXTERNAL, actual1.attrs.get(REF_EXTERNAL));

        Entity actual2 = subject.select(selectAttributes, INST2_ID);
        assertEquals(INST2_ID, actual2.id);
        assertEquals(DEFAULT_BOOLEAN, actual2.attrs.get(ATTR_BOOLEAN));
        assertEquals(DEFAULT_INTEGER, actual2.attrs.get(ATTR_INTEGER));
        assertEquals(DEFAULT_DOUBLE, actual2.attrs.get(ATTR_DOUBLE));
        assertEquals(DEFAULT_STRING, actual2.attrs.get(ATTR_STRING));
        assertEquals(DEFAULT_TEXT, actual2.attrs.get(ATTR_TEXT));
        assertEquals(DEFAULT_TIMESTAMP, actual2.attrs.get(ATTR_TIMESTAMP));
        assertEquals(DEFAULT_REF_INTEGER, actual2.attrs.get(REF_INTEGER));
        assertEquals(DEFAULT_REF_STRING, actual2.attrs.get(REF_STRING));
        assertEquals(DEFAULT_REF_EXTERNAL, actual2.attrs.get(REF_EXTERNAL));
    }

    private void updateInstance1() throws Exception {
        // Setup
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(ATTR_BOOLEAN, INST1_V2_BOOLEAN);
        attributes.put(ATTR_DOUBLE, INST1_V2_DOUBLE);
        attributes.put(ATTR_TEXT, INST1_V2_TEXT);
        attributes.put(REF_INTEGER, INST1_V2_REF_INTEGER);
        attributes.put(REF_EXTERNAL, INST1_V2_REF_EXTERNAL);

        DataOpUpdate op = new DataOpUpdate(INST1_ID, attributes);

        // Execute
        subject.update(op);

        // Validate
        EntityType type = model.getEntityType(ENTITY_TYPE);
        List<Attribute> selectAttributes = new ArrayList<>();
        selectAttributes.add(type.attrs.get(ATTR_IDENTITY));
        selectAttributes.add(type.attrs.get(ATTR_BOOLEAN));
        selectAttributes.add(type.attrs.get(ATTR_INTEGER));
        selectAttributes.add(type.attrs.get(ATTR_DOUBLE));
        selectAttributes.add(type.attrs.get(ATTR_STRING));
        selectAttributes.add(type.attrs.get(ATTR_TEXT));
        selectAttributes.add(type.attrs.get(ATTR_TIMESTAMP));
        selectAttributes.add(type.attrs.get(REF_INTEGER));
        selectAttributes.add(type.attrs.get(REF_STRING));
        selectAttributes.add(type.attrs.get(REF_EXTERNAL));

        Entity actual1 = subject.select(selectAttributes, INST1_ID);
        assertEquals(INST1_ID, actual1.id);
        assertEquals(INST1_V2_BOOLEAN, actual1.attrs.get(ATTR_BOOLEAN));
        assertEquals(INST1_V1_INTEGER, actual1.attrs.get(ATTR_INTEGER));
        assertEquals(INST1_V2_DOUBLE, actual1.attrs.get(ATTR_DOUBLE));
        assertEquals(INST1_V1_STRING, actual1.attrs.get(ATTR_STRING));
        assertEquals(INST1_V2_TEXT, actual1.attrs.get(ATTR_TEXT));
        assertEquals(INST1_V1_TIMESTAMP, actual1.attrs.get(ATTR_TIMESTAMP));
        assertEquals(INST1_V2_REF_INTEGER, actual1.attrs.get(REF_INTEGER));
        assertEquals(DEFAULT_REF_STRING, actual1.attrs.get(REF_STRING));
        assertEquals(INST1_V2_REF_EXTERNAL, actual1.attrs.get(REF_EXTERNAL));
    }

    private void updateInstance2() throws Exception {
        // Setup
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(ATTR_BOOLEAN, INST2_V2_BOOLEAN);
        attributes.put(ATTR_INTEGER, INST2_V2_INTEGER);
        attributes.put(ATTR_STRING, INST2_V2_STRING);
        attributes.put(ATTR_TIMESTAMP, INST2_V2_TIMESTAMP);
        attributes.put(REF_STRING, INST2_V2_REF_STRING);

        DataOpUpdate op = new DataOpUpdate(INST2_ID, attributes);

        // Execute
        subject.update(op);

        // Validate
        EntityType type = model.getEntityType(ENTITY_TYPE);
        List<Attribute> selectAttributes = new ArrayList<>();
        selectAttributes.add(type.attrs.get(ATTR_IDENTITY));
        selectAttributes.add(type.attrs.get(ATTR_BOOLEAN));
        selectAttributes.add(type.attrs.get(ATTR_INTEGER));
        selectAttributes.add(type.attrs.get(ATTR_DOUBLE));
        selectAttributes.add(type.attrs.get(ATTR_STRING));
        selectAttributes.add(type.attrs.get(ATTR_TEXT));
        selectAttributes.add(type.attrs.get(ATTR_TIMESTAMP));
        selectAttributes.add(type.attrs.get(REF_INTEGER));
        selectAttributes.add(type.attrs.get(REF_STRING));
        selectAttributes.add(type.attrs.get(REF_EXTERNAL));

        Entity actual2 = subject.select(selectAttributes, INST2_ID);
        assertEquals(INST2_ID, actual2.id);
        assertEquals(INST2_V2_BOOLEAN, actual2.attrs.get(ATTR_BOOLEAN));
        assertEquals(INST2_V2_INTEGER, actual2.attrs.get(ATTR_INTEGER));
        assertEquals(DEFAULT_DOUBLE, actual2.attrs.get(ATTR_DOUBLE));
        assertEquals(INST2_V2_STRING, actual2.attrs.get(ATTR_STRING));
        assertEquals(DEFAULT_TEXT, actual2.attrs.get(ATTR_TEXT));
        assertEquals(INST2_V2_TIMESTAMP, actual2.attrs.get(ATTR_TIMESTAMP));
        assertEquals(DEFAULT_REF_INTEGER, actual2.attrs.get(REF_INTEGER));
        assertEquals(INST2_V2_REF_STRING, actual2.attrs.get(REF_STRING));
        assertEquals(DEFAULT_REF_EXTERNAL, actual2.attrs.get(REF_EXTERNAL));
    }

    private void selectAttributesWithId() throws Exception {
        EntityType type = model.getEntityType(ENTITY_TYPE);
        List<Attribute> selectAttributes = new ArrayList<>();
        selectAttributes.add(type.attrs.get(ATTR_IDENTITY));
        selectAttributes.add(type.attrs.get(ATTR_INTEGER));
        selectAttributes.add(type.attrs.get(ATTR_STRING));
        selectAttributes.add(type.attrs.get(ATTR_TIMESTAMP));
        selectAttributes.add(type.attrs.get(REF_STRING));

        List<Entity> actuals = subject.select(selectAttributes, Arrays.asList(INST1_ID, INST2_ID, INST3_ID));
        assertEquals(2, actuals.size());

        Entity actual1 = findById(INST1_ID, actuals);
        Entity actual2 = findById(INST2_ID, actuals);
        assertNotNull(actual1);
        assertNotNull(actual2);

        assertEquals(INST1_ID, actual1.id);
        assertEquals(5, actual1.attrs.size());
        assertEquals(null, actual1.attrs.get(ATTR_BOOLEAN));
        assertEquals(INST1_V1_INTEGER, actual1.attrs.get(ATTR_INTEGER));
        assertEquals(null, actual1.attrs.get(ATTR_DOUBLE));
        assertEquals(INST1_V1_STRING, actual1.attrs.get(ATTR_STRING));
        assertEquals(null, actual1.attrs.get(ATTR_TEXT));
        assertEquals(INST1_V1_TIMESTAMP, actual1.attrs.get(ATTR_TIMESTAMP));
        assertEquals(null, actual1.attrs.get(REF_INTEGER));
        assertEquals(DEFAULT_REF_STRING, actual1.attrs.get(REF_STRING));
        assertEquals(null, actual1.attrs.get(REF_EXTERNAL));

        assertEquals(INST2_ID, actual2.id);
        assertEquals(5, actual1.attrs.size());
        assertEquals(null, actual2.attrs.get(ATTR_BOOLEAN));
        assertEquals(INST2_V2_INTEGER, actual2.attrs.get(ATTR_INTEGER));
        assertEquals(null, actual2.attrs.get(ATTR_DOUBLE));
        assertEquals(INST2_V2_STRING, actual2.attrs.get(ATTR_STRING));
        assertEquals(null, actual2.attrs.get(ATTR_TEXT));
        assertEquals(INST2_V2_TIMESTAMP, actual2.attrs.get(ATTR_TIMESTAMP));
        assertEquals(null, actual2.attrs.get(REF_INTEGER));
        assertEquals(INST2_V2_REF_STRING, actual2.attrs.get(REF_STRING));
        assertEquals(null, actual2.attrs.get(REF_EXTERNAL));
    }

    private void selectAttributesWithoutId() throws Exception {
        EntityType type = model.getEntityType(ENTITY_TYPE);
        List<Attribute> selectAttributes = new ArrayList<>();
        selectAttributes.add(type.attrs.get(ATTR_BOOLEAN));
        selectAttributes.add(type.attrs.get(ATTR_DOUBLE));
        selectAttributes.add(type.attrs.get(ATTR_TEXT));
        selectAttributes.add(type.attrs.get(REF_INTEGER));
        selectAttributes.add(type.attrs.get(REF_EXTERNAL));

        List<Entity> actuals = subject.select(selectAttributes, Arrays.asList(INST1_ID, INST2_ID, INST3_ID));
        assertEquals(2, actuals.size());

        Entity actual1 = findById(INST1_ID, actuals);
        Entity actual2 = findById(INST2_ID, actuals);
        assertNotNull(actual1);
        assertNotNull(actual2);

        assertEquals(INST1_ID, actual1.id);
        assertEquals(5, actual1.attrs.size());
        assertEquals(INST1_V2_BOOLEAN, actual1.attrs.get(ATTR_BOOLEAN));
        assertEquals(null, actual1.attrs.get(ATTR_INTEGER));
        assertEquals(INST1_V2_DOUBLE, actual1.attrs.get(ATTR_DOUBLE));
        assertEquals(null, actual1.attrs.get(ATTR_STRING));
        assertEquals(INST1_V2_TEXT, actual1.attrs.get(ATTR_TEXT));
        assertEquals(null, actual1.attrs.get(ATTR_TIMESTAMP));
        assertEquals(INST1_V2_REF_INTEGER, actual1.attrs.get(REF_INTEGER));
        assertEquals(null, actual1.attrs.get(REF_STRING));
        assertEquals(INST1_V2_REF_EXTERNAL, actual1.attrs.get(REF_EXTERNAL));

        assertEquals(INST2_ID, actual2.id);
        assertEquals(5, actual1.attrs.size());
        assertEquals(INST2_V2_BOOLEAN, actual2.attrs.get(ATTR_BOOLEAN));
        assertEquals(null, actual2.attrs.get(ATTR_INTEGER));
        assertEquals(DEFAULT_DOUBLE, actual2.attrs.get(ATTR_DOUBLE));
        assertEquals(null, actual2.attrs.get(ATTR_STRING));
        assertEquals(DEFAULT_TEXT, actual2.attrs.get(ATTR_TEXT));
        assertEquals(null, actual2.attrs.get(ATTR_TIMESTAMP));
        assertEquals(DEFAULT_REF_INTEGER, actual2.attrs.get(REF_INTEGER));
        assertEquals(null, actual2.attrs.get(REF_STRING));
        assertEquals(DEFAULT_REF_EXTERNAL, actual2.attrs.get(REF_EXTERNAL));
    }

    private void selectWithAttributesCondition() throws Exception {
        EntityType type = model.getEntityType(ENTITY_TYPE);

        Pair<Attribute, Collection<?>> condition = new ImmutablePair<>(type.attrs.get(ATTR_BOOLEAN),
                                                                       Arrays.asList(DEFAULT_BOOLEAN));
        List<Entity> entities = subject.select(null, type, Arrays.asList(condition), null);
        assertEquals(1, entities.size());
        assertNotNull(findById(INST1_ID, entities));

        condition = new ImmutablePair<>(type.attrs.get(ATTR_INTEGER),
                                        Arrays.asList(INST2_V2_INTEGER));
        entities = subject.select(null, type, Arrays.asList(condition), null);
        assertEquals(1, entities.size());
        assertNotNull(findById(INST2_ID, entities));

        condition = new ImmutablePair<>(type.attrs.get(ATTR_DOUBLE),
                                        Arrays.asList(INST1_V2_DOUBLE));
        entities = subject.select(null, type, Arrays.asList(condition), null);
        assertEquals(1, entities.size());
        assertNotNull(findById(INST1_ID, entities));

        condition = new ImmutablePair<>(type.attrs.get(ATTR_STRING),
                                        Arrays.asList(INST2_V2_STRING));
        entities = subject.select(null, type, Arrays.asList(condition), null);
        assertEquals(1, entities.size());
        assertNotNull(findById(INST2_ID, entities));

        condition = new ImmutablePair<>(type.attrs.get(ATTR_TEXT),
                                        Arrays.asList(INST1_V2_TEXT));
        entities = subject.select(null, type, Arrays.asList(condition), null);
        assertEquals(1, entities.size());
        assertNotNull(findById(INST1_ID, entities));

        condition = new ImmutablePair<>(type.attrs.get(ATTR_TIMESTAMP),
                                        Arrays.asList(INST2_V2_TIMESTAMP));
        entities = subject.select(null, type, Arrays.asList(condition), null);
        assertEquals(1, entities.size());
        assertNotNull(findById(INST2_ID, entities));

        condition = new ImmutablePair<>(type.attrs.get(REF_INTEGER),
                                        Arrays.asList(INST1_V2_REF_INTEGER));
        entities = subject.select(null, type, Arrays.asList(condition), null);
        assertEquals(1, entities.size());
        assertNotNull(findById(INST1_ID, entities));

        condition = new ImmutablePair<>(type.attrs.get(REF_STRING),
                                        Arrays.asList(INST2_V2_REF_STRING));
        entities = subject.select(null, type, Arrays.asList(condition), null);
        assertEquals(1, entities.size());
        assertNotNull(findById(INST2_ID, entities));

        condition = new ImmutablePair<>(type.attrs.get(REF_EXTERNAL),
                                        Arrays.asList(INST1_V2_REF_EXTERNAL));
        entities = subject.select(null, type, Arrays.asList(condition), null);
        assertEquals(1, entities.size());
        assertNotNull(findById(INST1_ID, entities));
    }

    private void selectWithAttributesSorting() throws Exception {
        EntityType type = model.getEntityType(ENTITY_TYPE);

        Pair<Attribute, Boolean> sorting = new ImmutablePair<>(type.attrs.get(ATTR_BOOLEAN), true);
        List<Entity> entities = subject.select(null, type, null, Arrays.asList(sorting));
        assertEquals(2, entities.size());
        assertEquals(INST2_ID, entities.get(0).id); // INST2_V2_BOOLEAN = FALSE
        assertEquals(INST1_ID, entities.get(1).id); // INST1_V2_BOOLEAN = TRUE

        sorting = new ImmutablePair<>(type.attrs.get(ATTR_INTEGER), true);
        entities = subject.select(null, type, null, Arrays.asList(sorting));
        assertEquals(2, entities.size());
        assertEquals(INST1_ID, entities.get(0).id); // INST1_V1_INTEGER = 2L
        assertEquals(INST2_ID, entities.get(1).id); // INST2_V2_INTEGER = 6L

        sorting = new ImmutablePair<>(type.attrs.get(ATTR_DOUBLE), true);
        entities = subject.select(null, type, null, Arrays.asList(sorting));
        assertEquals(2, entities.size());
        assertEquals(INST2_ID, entities.get(0).id); // DEFAULT_DOUBLE = 2.3
        assertEquals(INST1_ID, entities.get(1).id); // INST1_V2_DOUBLE = 4.5

        sorting = new ImmutablePair<>(type.attrs.get(ATTR_STRING), false);
        entities = subject.select(null, type, null, Arrays.asList(sorting));
        assertEquals(2, entities.size());
        assertEquals(INST2_ID, entities.get(0).id); // INST2_V2_STRING = "Welcome"
        assertEquals(INST1_ID, entities.get(1).id); // INST1_V1_STRING = "Hi";

        sorting = new ImmutablePair<>(type.attrs.get(ATTR_TEXT), false);
        entities = subject.select(null, type, null, Arrays.asList(sorting));
        assertEquals(2, entities.size());
        assertEquals(INST1_ID, entities.get(0).id); // INST1_V2_TEXT = "Text V2!";
        assertEquals(INST2_ID, entities.get(1).id); // DEFAULT_TEXT = "Hello 'Text'!";

        sorting = new ImmutablePair<>(type.attrs.get(ATTR_TIMESTAMP), true);
        entities = subject.select(null, type, null, Arrays.asList(sorting));
        assertEquals(2, entities.size());
        assertEquals(INST1_ID, entities.get(0).id); // INST1_V1_TIMESTAMP = new Date(1234509876L);
        assertEquals(INST2_ID, entities.get(1).id); // INST2_V2_TIMESTAMP = new Date(6789012345L);

        sorting = new ImmutablePair<>(type.attrs.get(REF_INTEGER), true);
        entities = subject.select(null, type, null, Arrays.asList(sorting));
        assertEquals(2, entities.size());
        assertEquals(INST2_ID, entities.get(0).id); // DEFAULT_REF_INTEGER  = new ReferenceInteger(ENTITY_TYPE, 22);
        assertEquals(INST1_ID, entities.get(1).id); // INST1_V2_REF_INTEGER = new ReferenceInteger(ENTITY_TYPE, 23);

        sorting = new ImmutablePair<>(type.attrs.get(REF_STRING), false);
        entities = subject.select(null, type, null, Arrays.asList(sorting));
        assertEquals(2, entities.size());
        assertEquals(INST2_ID, entities.get(0).id); // INST2_V2_REF_STRING = new ReferenceString(OTHER_TYPE, "second");
        assertEquals(INST1_ID, entities.get(1).id); // DEFAULT_REF_STRING  = new ReferenceString(OTHER_TYPE, "first");

        sorting = new ImmutablePair<>(type.attrs.get(REF_EXTERNAL), false);
        entities = subject.select(null, type, null, Arrays.asList(sorting));
        assertEquals(2, entities.size());
        assertEquals(INST1_ID, entities.get(0).id); // INST1_V2_REF_EXTERNAL = new ReferenceExternal("http://example.com/v2");
        assertEquals(INST2_ID, entities.get(1).id); // DEFAULT_REF_EXTERNAL = new ReferenceExternal("http://example.com");
    }

    private void removeAttributesFromType() throws Exception {
        ModelOpDeleteAttribute op1 = new ModelOpDeleteAttribute(ENTITY_TYPE, ATTR_BOOLEAN);
        ModelOpDeleteAttribute op2 = new ModelOpDeleteAttribute(ENTITY_TYPE, ATTR_INTEGER);
        ModelOpDeleteAttribute op3 = new ModelOpDeleteAttribute(ENTITY_TYPE, ATTR_DOUBLE);
        ModelOpDeleteAttribute op4 = new ModelOpDeleteAttribute(ENTITY_TYPE, ATTR_STRING);
        ModelOpDeleteAttribute op5 = new ModelOpDeleteAttribute(ENTITY_TYPE, ATTR_TEXT);
        ModelOpDeleteAttribute op6 = new ModelOpDeleteAttribute(ENTITY_TYPE, ATTR_TIMESTAMP);

        PatchModel patch = new PatchModel(Arrays.asList(op1, op2, op3, op4, op5, op6), null, null, null, 0L, 0L);
        model.apply(patch);
        subject.apply(patch);

        EntityType type = model.getEntityType(ENTITY_TYPE);
        List<Attribute> selectAttributes = new ArrayList<>();
        selectAttributes.add(type.attrs.get(REF_INTEGER));
        selectAttributes.add(type.attrs.get(REF_STRING));
        selectAttributes.add(type.attrs.get(REF_EXTERNAL));

        Entity actual1 = subject.select(selectAttributes, INST1_ID);
        assertEquals(INST1_ID, actual1.id);
        assertEquals(3, actual1.attrs.size());
        assertEquals(INST1_V2_REF_INTEGER, actual1.attrs.get(REF_INTEGER));
        assertEquals(DEFAULT_REF_STRING, actual1.attrs.get(REF_STRING));
        assertEquals(INST1_V2_REF_EXTERNAL, actual1.attrs.get(REF_EXTERNAL));

        Entity actual2 = subject.select(selectAttributes, INST2_ID);
        assertEquals(INST2_ID, actual2.id);
        assertEquals(3, actual2.attrs.size());
        assertEquals(DEFAULT_REF_INTEGER, actual2.attrs.get(REF_INTEGER));
        assertEquals(INST2_V2_REF_STRING, actual2.attrs.get(REF_STRING));
        assertEquals(DEFAULT_REF_EXTERNAL, actual2.attrs.get(REF_EXTERNAL));
    }

    private void deleteInstance1() throws Exception {
        DataOpDelete op = new DataOpDelete(INST1_ID);

        subject.apply(op);

        EntityType type = model.getEntityType(ENTITY_TYPE);
        List<Attribute> selectAttributes = new ArrayList<>();
        selectAttributes.add(type.attrs.get(REF_INTEGER));
        selectAttributes.add(type.attrs.get(REF_STRING));
        selectAttributes.add(type.attrs.get(REF_EXTERNAL));

        Entity actual1 = subject.select(selectAttributes, INST1_ID);
        assertNull(actual1);

        Entity actual2 = subject.select(selectAttributes, INST2_ID);
        assertEquals(INST2_ID, actual2.id);
        assertEquals(3, actual2.attrs.size());
        assertEquals(DEFAULT_REF_INTEGER, actual2.attrs.get(REF_INTEGER));
        assertEquals(INST2_V2_REF_STRING, actual2.attrs.get(REF_STRING));
        assertEquals(DEFAULT_REF_EXTERNAL, actual2.attrs.get(REF_EXTERNAL));
    }

    private void deleteType() throws Exception {
        ModelOpDeleteType op = new ModelOpDeleteType(ENTITY_TYPE);

        model.apply(op);
        subject.apply(op);

        assertFalse(subject.isTypeExists(ENTITY_TYPE));
    }

    private Entity findById(ReferenceInteger id, List<Entity> entities) {
        for (Entity entity : entities) {
            if (id.equals(entity.id)) {
                return entity;
            }
        }
        return null;
    }

}
