package org.state.patch.sql.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.state.patch.sql.data.Entity;
import org.state.patch.sql.data.ReferenceInteger;
import org.state.patch.sql.data.ReferenceInternal;
import org.state.patch.sql.model.Attribute;
import org.state.patch.sql.model.EntityType;
import org.state.patch.sql.model.Model;
import org.state.patch.sql.model.PrimitiveType;
import org.state.patch.sql.model.ReferenceType;
import org.state.patch.sql.model.op.ModelOp;
import org.state.patch.sql.model.op.ModelOpCreateType;

public class DatabaseLoader_UnitTets {

    static final String type        = "entity";
    static final String typeUnknown = "unknown";
    static final String idName      = "id";

    static final ReferenceInternal id1 = new ReferenceInteger(type, 123L);
    static final ReferenceInternal id2 = new ReferenceInteger(type, 456L);
    static final ReferenceInternal id3 = new ReferenceInteger(typeUnknown, 789L);

    static final Entity entity1 = new Entity(id1, null);
    static final Entity entity2 = new Entity(id2, null);

    static final List<Attribute> attributesRequest = new ArrayList<>();

    final Model model = new Model();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setupModel() {
        ModelOp.Attribute identity = new ModelOp.Attribute(idName, new ReferenceType(type, PrimitiveType.INTEGER), null);
        model.createType(new ModelOpCreateType(type, identity, new ArrayList<>()));
    }

    @Test
    public void select_byId() throws Exception {
        // Setup
        final List<Entity> result = Arrays.asList(entity1);

        DatabaseLoad subject = new DatabaseLoad() {
            @Override
            public List<Entity> select(Collection<Attribute> attributes,
                                       EntityType entityType,
                                       Collection<Pair<Attribute, Collection<?>>> conditions,
                                       Collection<Pair<Attribute, Boolean>> sortings) throws Exception {
                assertSame(attributesRequest, attributes);
                assertSame(type, entityType.name);
                assertEquals(1, conditions.size());
                Pair<Attribute, Collection<?>> condition = conditions.iterator().next();
                assertEquals(idName, condition.getLeft().name);
                assertEquals(1, condition.getRight().size());
                assertEquals(id1, condition.getRight().iterator().next());
                assertEquals(null, sortings);
                return result;
            }

            @Override
            public Model getModel() {
                return model;
            }
        };

        // Execute & Validate
        assertSame(entity1, subject.select(attributesRequest, id1));
    }

    @Test
    public void select_byId_noResult() throws Exception {
        // Setup
        final List<Entity> result = Arrays.asList();

        DatabaseLoad subject = new DatabaseLoad() {
            @Override
            public List<Entity> select(Collection<Attribute> attributes,
                                       EntityType entityType,
                                       Collection<Pair<Attribute, Collection<?>>> conditions,
                                       Collection<Pair<Attribute, Boolean>> sortings) throws Exception {
                assertSame(attributesRequest, attributes);
                assertSame(type, entityType.name);
                assertEquals(1, conditions.size());
                Pair<Attribute, Collection<?>> condition = conditions.iterator().next();
                assertEquals(idName, condition.getLeft().name);
                assertEquals(1, condition.getRight().size());
                assertEquals(id1, condition.getRight().iterator().next());
                assertEquals(null, sortings);
                return result;
            }

            @Override
            public Model getModel() {
                return model;
            }
        };

        // Execute & Validate
        assertSame(null, subject.select(attributesRequest, id1));
    }

    @Test
    public void select_byId_null() throws Exception {
        // Setup
        DatabaseLoad subject = new DatabaseLoad() {
            @Override
            public List<Entity> select(Collection<Attribute> attributes,
                                       EntityType entityType,
                                       Collection<Pair<Attribute, Collection<?>>> conditions,
                                       Collection<Pair<Attribute, Boolean>> sortings) throws Exception {
                fail("Shouldn't be called");
                return null;
            }

            @Override
            public Model getModel() {
                return model;
            }
        };

        // Execute & Validate
        assertEquals(null, subject.select(attributesRequest, (ReferenceInternal) null));
    }

    @Test
    public void select_byIds() throws Exception {
        // Setup
        final List<Entity> result = Arrays.asList(entity1, entity2);

        DatabaseLoad subject = new DatabaseLoad() {
            @Override
            public List<Entity> select(Collection<Attribute> attributes,
                                       EntityType entityType,
                                       Collection<Pair<Attribute, Collection<?>>> conditions,
                                       Collection<Pair<Attribute, Boolean>> sortings) throws Exception {
                assertSame(attributesRequest, attributes);
                assertSame(type, entityType.name);
                assertEquals(1, conditions.size());
                Pair<Attribute, Collection<?>> condition = conditions.iterator().next();
                assertEquals(idName, condition.getLeft().name);
                assertEquals(2, condition.getRight().size());
                Iterator<?> it = condition.getRight().iterator();
                assertEquals(id1, it.next());
                assertEquals(id2, it.next());
                assertEquals(null, sortings);
                return result;
            }

            @Override
            public Model getModel() {
                return model;
            }
        };

        // Execute & Validate
        assertSame(result, subject.select(attributesRequest, Arrays.asList(id1, id2)));
    }

    @Test
    public void select_byIds_null() throws Exception {
        // Setup
        DatabaseLoad subject = new DatabaseLoad() {
            @Override
            public List<Entity> select(Collection<Attribute> attributes,
                                       EntityType entityType,
                                       Collection<Pair<Attribute, Collection<?>>> conditions,
                                       Collection<Pair<Attribute, Boolean>> sortings) throws Exception {
                fail("Shouldn't be called");
                return null;
            }

            @Override
            public Model getModel() {
                return model;
            }
        };

        // Execute & Validate
        assertEquals(0, subject.select(attributesRequest, (Collection<ReferenceInternal>) null).size());
    }

    @Test
    public void select_byIds_empty() throws Exception {
        // Setup
        DatabaseLoad subject = new DatabaseLoad() {
            @Override
            public List<Entity> select(Collection<Attribute> attributes,
                                       EntityType entityType,
                                       Collection<Pair<Attribute, Collection<?>>> conditions,
                                       Collection<Pair<Attribute, Boolean>> sortings) throws Exception {
                fail("Shouldn't be called");
                return null;
            }

            @Override
            public Model getModel() {
                return model;
            }
        };

        // Execute & Validate
        assertEquals(0, subject.select(attributesRequest, Collections.emptyList()).size());
    }

    @Test
    public void select_byIds_mixedTypes() throws Exception {
        // Setup
        DatabaseLoad subject = new DatabaseLoad() {
            @Override
            public List<Entity> select(Collection<Attribute> attributes,
                                       EntityType entityType,
                                       Collection<Pair<Attribute, Collection<?>>> conditions,
                                       Collection<Pair<Attribute, Boolean>> sortings) throws Exception {
                fail("Shouldn't be called");
                return null;
            }

            @Override
            public Model getModel() {
                return model;
            }
        };

        exception.expect(RuntimeException.class);
        exception.expectMessage("Mix type reference requests is not supported.");

        // Execute & Validate
        assertEquals(0, subject.select(attributesRequest, Arrays.asList(id2, id3)).size());
    }

    @Test
    public void select_byIds_unknownType() throws Exception {
        // Setup
        DatabaseLoad subject = new DatabaseLoad() {
            @Override
            public List<Entity> select(Collection<Attribute> attributes,
                                       EntityType entityType,
                                       Collection<Pair<Attribute, Collection<?>>> conditions,
                                       Collection<Pair<Attribute, Boolean>> sortings) throws Exception {
                fail("Shouldn't be called");
                return null;
            }

            @Override
            public Model getModel() {
                return model;
            }
        };

        exception.expect(RuntimeException.class);
        exception.expectMessage("Entity type " + typeUnknown + " is not defined in model.");

        // Execute & Validate
        assertEquals(0, subject.select(attributesRequest, Arrays.asList(id3)).size());
    }
}
