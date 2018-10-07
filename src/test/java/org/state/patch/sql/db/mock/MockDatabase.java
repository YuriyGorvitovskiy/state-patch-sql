package org.state.patch.sql.db.mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.state.patch.sql.data.Entity;
import org.state.patch.sql.data.Reference;
import org.state.patch.sql.data.ReferenceExternal;
import org.state.patch.sql.data.ReferenceInteger;
import org.state.patch.sql.data.ReferenceInternal;
import org.state.patch.sql.data.ReferenceString;
import org.state.patch.sql.data.op.DataOpDelete;
import org.state.patch.sql.data.op.DataOpInsert;
import org.state.patch.sql.data.op.DataOpUpdate;
import org.state.patch.sql.db.Database;
import org.state.patch.sql.model.Attribute;
import org.state.patch.sql.model.EntityType;
import org.state.patch.sql.model.Model;
import org.state.patch.sql.model.PrimitiveType;
import org.state.patch.sql.model.ReferenceType;
import org.state.patch.sql.model.ValueType;
import org.state.patch.sql.model.op.ModelOpAppendAttribute;
import org.state.patch.sql.model.op.ModelOpCreateType;
import org.state.patch.sql.model.op.ModelOpDeleteAttribute;
import org.state.patch.sql.model.op.ModelOpDeleteType;

public class MockDatabase implements Database {

    public Model                  model;
    public Map<Reference, Entity> data;

    public MockDatabase() {
        this(new Model(), new HashMap<>());
    }

    /** The model should not be the same as application model, it represents internal schema
     * @param model
     * @param data
     */
    public MockDatabase(Model model, Map<Reference, Entity> data) {
        this.model = model;
        this.data = data;
    }

    @Override
    public boolean isTypeExists(String type) throws Exception {
        return null != model.getEntityType(type);
    }

    @Override
    public void createType(ModelOpCreateType op) throws Exception {
        assertNull(model.getEntityType(op.type));
        model.createType(op);
    }

    @Override
    public void deleteType(ModelOpDeleteType op) throws Exception {
        assertNotNull(model.getEntityType(op.type));
        model.deleteType(op);
    }

    @Override
    public void appendAttribute(ModelOpAppendAttribute op) throws Exception {
        EntityType type = model.getEntityType(op.type);
        assertNotNull(type);
        assertNull(type.attrs.get(op.attr.name));
        model.appendAttribute(op);
    }

    @Override
    public void deleteAttribute(ModelOpDeleteAttribute op) throws Exception {
        EntityType type = model.getEntityType(op.type);
        assertNotNull(type);
        assertNotNull(type.attrs.get(op.attribName));
        model.deleteAttribute(op);
    }

    @Override
    public void insert(DataOpInsert op) throws Exception {
        EntityType entityType = model.getEntityType(op.id.type);
        assertNotNull(entityType);
        assertValueType(entityType.identity.type, op.id);

        Map<String, Object> entityAttrs = new HashMap<>();
        for (Attribute modelAttr : entityType.attrs.values()) {
            entityAttrs.put(modelAttr.name, modelAttr.initial);
        }

        entityAttrs.put(entityType.identity.name, op.id);
        for (Map.Entry<String, Object> opAttr : op.attrs.entrySet()) {
            Attribute modelAttr = entityType.attrs.get(opAttr.getKey());
            assertNotNull(modelAttr);
            assertValueType(modelAttr.type, opAttr.getValue());
            entityAttrs.put(opAttr.getKey(), opAttr.getValue());
        }

        Entity entity = new Entity(op.id, entityAttrs);
        assertNull(data.put(op.id, entity));
    }

    @Override
    public void update(DataOpUpdate op) throws Exception {
        EntityType entityType = model.getEntityType(op.id.type);
        assertNotNull(entityType);
        assertValueType(entityType.identity.type, op.id);

        Entity entity = data.get(op.id);
        assertNotNull(entity);

        for (Map.Entry<String, Object> opAttr : op.attrs.entrySet()) {
            Attribute modelAttr = entityType.attrs.get(opAttr.getKey());
            assertNotNull(modelAttr);
            assertValueType(modelAttr.type, opAttr.getValue());
            entity.attrs.put(opAttr.getKey(), opAttr.getValue());
        }
    }

    @Override
    public void delete(DataOpDelete op) throws Exception {
        EntityType entityType = model.getEntityType(op.id.type);
        assertNotNull(entityType);
        assertValueType(entityType.identity.type, op.id);

        Entity entity = data.remove(op.id);
        assertNotNull(entity);
    }

    @Override
    public List<Entity> select(Collection<Attribute> attributes, EntityType entityType,
                               Collection<Pair<Attribute, Collection<?>>> conditions,
                               Collection<Pair<Attribute, Boolean>> sortings) throws Exception {
        List<Entity> result = new ArrayList<>();
        for (Entity entity : data.values()) {
            if (!entityType.name.equals(entity.id.type)) {
                continue;
            }
            if (!checkConditions(conditions, entity)) {
                continue;
            }
            result.add(entity);
        }
        result.sort(createComporator(sortings));
        return result;
    }

    @Override
    public Model getModel() {
        return model;
    }

    void assertValueType(ValueType type, Object value) {
        if (null == value) {
            return;
        }

        if (type instanceof PrimitiveType) {
            switch ((PrimitiveType) type) {
                case BOOLEAN:
                    assertTrue(value instanceof Boolean);
                    return;
                case DOUBLE:
                case INTEGER:
                    assertTrue(value instanceof Number);
                    return;
                case REFERENCE_EXTERNAL:
                    assertTrue(value instanceof ReferenceExternal);
                    return;
                case STRING:
                case TEXT:
                    assertTrue(value instanceof String);
                    return;
                case TIMESTAMP:
                    assertTrue(value instanceof Date);
                    return;
            }
        }

        if (type instanceof ReferenceType) {
            ReferenceType refType = (ReferenceType) type;
            assertTrue(value instanceof ReferenceInternal);
            ReferenceInternal ref = (ReferenceInternal) value;
            assertEquals(refType.entityType, ref.type);
            if (refType.storageType == PrimitiveType.INTEGER) {
                assertTrue(ref instanceof ReferenceInteger);
                return;
            } else if (refType.storageType == PrimitiveType.STRING) {
                assertTrue(ref instanceof ReferenceString);
                return;
            }
        }
        fail("Unsupprted value type: " + type);
    }

    boolean checkConditions(Collection<Pair<Attribute, Collection<?>>> conditions, Entity entity) {
        if (null != conditions) {
            for (Pair<Attribute, Collection<?>> condition : conditions) {
                if (!checkCondition(condition, entity)) {
                    return false;
                }
            }
        }
        return true;
    }

    boolean checkCondition(Pair<Attribute, Collection<?>> condition, Entity entity) {
        Object attrValue = entity.attrs.get(condition.getLeft().name);
        for (Object compValue : condition.getRight()) {
            if (Objects.equals(attrValue, compValue))
                return true;
        }
        return false;
    }

    Comparator<? super Entity> createComporator(Collection<Pair<Attribute, Boolean>> sortings) {
        final List<Pair<String, Comparator<Object>>> comparators = new ArrayList<>();
        if (null == sortings || sortings.isEmpty()) {
            return (a, b) -> Integer.compare(Objects.hashCode(a), Objects.hashCode(b));
        }
        for (Pair<Attribute, Boolean> sorting : sortings) {
            Attribute attr = sorting.getLeft();
            Pair<String, Comparator<Object>> comp = new ImmutablePair<>(attr.name,
                                                                        createComporator(attr.type, sorting.getRight()));
            comparators.add(comp);
        }
        return (a, b) -> {
            for (Pair<String, Comparator<Object>> comparator : comparators) {
                int res = comparator.getRight().compare(a.attrs.get(comparator.getLeft()),
                                                        b.attrs.get(comparator.getLeft()));
                if (res != 0) {
                    return res;
                }
            }
            return 0;
        };
    }

    private Comparator<Object> createComporator(ValueType type, boolean ascending) {
        final Comparator<Object> defaultComparator = createComporator(type);
        return ascending ? defaultComparator
                         : (a, b) -> {
                             return -1 * defaultComparator.compare(a, b);
                         };
    }

    private Comparator<Object> createComporator(ValueType type) {
        if (type instanceof PrimitiveType) {
            switch ((PrimitiveType) type) {
                case BOOLEAN:
                    return (a, b) -> {
                        if (a == b) {
                            return 0;
                        } else if (null == a) {
                            return -1;
                        } else if (null == b) {
                            return -1;
                        } else {
                            return Boolean.compare((Boolean) a, (Boolean) b);
                        }
                    };
                case INTEGER:
                case DOUBLE:
                    return (a, b) -> {
                        if (a == b) {
                            return 0;
                        } else if (null == a) {
                            return -1;
                        } else if (null == b) {
                            return -1;
                        } else {
                            return Double.compare(((Number) a).doubleValue(), ((Number) a).doubleValue());
                        }
                    };
                case REFERENCE_EXTERNAL:
                case STRING:
                case TEXT:
                    return (a, b) -> {
                        if (a == b) {
                            return 0;
                        } else if (null == a) {
                            return -1;
                        } else if (null == b) {
                            return -1;
                        } else {
                            return Objects.toString(a).compareTo(Objects.toString(b));
                        }
                    };
                case TIMESTAMP:
                    return (a, b) -> {
                        if (a == b) {
                            return 0;
                        } else if (null == a) {
                            return -1;
                        } else if (null == b) {
                            return -1;
                        } else {
                            return Long.compare(((Date) a).getTime(), ((Date) b).getTime());
                        }
                    };
            }
        }

        if (type instanceof ReferenceType) {
            return (a, b) -> {
                if (a == b) {
                    return 0;
                } else if (null == a) {
                    return -1;
                } else if (null == b) {
                    return -1;
                } else {
                    return Objects.toString(a).compareTo(Objects.toString(b));
                }
            };
        }
        fail("Unsupprted value type: " + type);
        return null;
    }

}
