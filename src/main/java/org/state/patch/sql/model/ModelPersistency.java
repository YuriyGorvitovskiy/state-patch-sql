package org.state.patch.sql.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.state.patch.sql.config.ModelConfig;
import org.state.patch.sql.data.Entity;
import org.state.patch.sql.data.Reference;
import org.state.patch.sql.data.ReferenceExternal;
import org.state.patch.sql.data.ReferenceInternal;
import org.state.patch.sql.data.ReferenceString;
import org.state.patch.sql.data.op.DataOp;
import org.state.patch.sql.data.op.DataOpDelete;
import org.state.patch.sql.data.op.DataOpInsert;
import org.state.patch.sql.db.Database;
import org.state.patch.sql.model.op.ModelOp;
import org.state.patch.sql.model.op.ModelOpAppendAttribute;
import org.state.patch.sql.model.op.ModelOpCreateType;
import org.state.patch.sql.model.op.ModelOpDeleteAttribute;
import org.state.patch.sql.model.op.ModelOpDeleteType;
import org.state.patch.sql.patch.JsonPatchTranslator;
import org.state.patch.sql.patch.PatchData;
import org.state.patch.sql.patch.PatchModelProcessor;

import com.fasterxml.jackson.databind.util.StdDateFormat;

public class ModelPersistency implements PatchModelProcessor {
    static interface ModelType {
        public static final String VERSION   = "version";
        public static final String ATTRIBUTE = "attribute";
    }

    static interface VersionAttr {
        public static final String ID  = "id";
        public static final String TAG = "tag";
    }

    static interface AttributeAttr {
        public static final String ID          = "id";
        public static final String ENTITY_TYPE = "entity_type";
        public static final String ATTR_NAME   = "attr_name";
        public static final String IDENTITY    = "identity";
        public static final String VALUE_TYPE  = "value_type";
        public static final String INITIAL     = "intial";
    }

    static interface Ref {
        public static final ReferenceInternal MODEL_VERSION = new ReferenceString(ModelType.VERSION, "model-of-model");
    }

    static interface Value {
        public static final String PRIMITIVE = "PRIMITIVE";
        public static final String REFERENCE = "REFERENCE";
    }

    static final String        TYPE_ATTR_SEP  = ".";
    static final String        VALUE_TYPE_SEP = ":";
    static final StdDateFormat DATE_FORMAT    = new StdDateFormat();

    enum Version {
        EMPTY_DATABASE("0.0.0"),
        CURRENT("0.0.1");

        public final String tag;

        Version(String tag) {
            this.tag = tag;
        }

        static Version byTag(String tag) {
            for (Version version : Version.values()) {
                if (version.tag.equals(tag)) {
                    return version;
                }
            }
            throw new RuntimeException("Unknown model version: " + tag);
        }
    }

    final Model               modelModel;
    final Database            modelDatabase;
    final JsonPatchTranslator jsonTranslator;

    public ModelPersistency(ModelConfig config) {
        this(config, new Model());
    }

    protected ModelPersistency(ModelConfig config, Model modelModel) {
        this(modelModel, Database.create(modelModel, config.database), new JsonPatchTranslator(modelModel));
    }

    protected ModelPersistency(Model modelModel,
                               Database modelDatabase,
                               JsonPatchTranslator jsonTranslator) {
        this.modelModel = modelModel;
        this.modelDatabase = modelDatabase;
        this.jsonTranslator = jsonTranslator;
    }

    public void initialize() throws Exception {
        modelModel.loadFromResource(ModelPersistency.class, "model-of-model.json", jsonTranslator);

        Version version = getVersion();
        switch (version) {
            case EMPTY_DATABASE: {
                modelDatabase.loadFromResource(ModelPersistency.class, "model-of-model.json", jsonTranslator);
                insertVersion(Version.CURRENT);
                break;
            }
            case CURRENT:
                break;
        }
    }

    private Version getVersion() throws Exception {
        if (!modelDatabase.isTypeExists(ModelType.VERSION)) {
            return Version.EMPTY_DATABASE;
        }

        EntityType versionType = modelModel.getEntityType(ModelType.VERSION);
        Entity versionEntity = modelDatabase.select(Collections.singleton(versionType.attrs.get(VersionAttr.TAG)),
                                                    Ref.MODEL_VERSION);

        String tag = (String) versionEntity.attrs.get(VersionAttr.TAG);
        return Version.byTag(tag);
    }

    private void insertVersion(Version version) throws Exception {
        Map<String, Object> attrs = new HashMap<>();
        attrs.put(VersionAttr.TAG, version.tag);

        DataOpInsert op = new DataOpInsert(Ref.MODEL_VERSION, attrs);
        modelDatabase.insert(op);
    }

    @Override
    public void createType(ModelOpCreateType op) throws Exception {
        PatchData patch = toPatchData(op);
        modelDatabase.apply(patch);
    }

    @Override
    public void deleteType(ModelOpDeleteType modelOp) throws Exception {
        EntityType attributeType = modelModel.getEntityType(ModelType.ATTRIBUTE);
        Attribute entityTypeAttr = attributeType.attrs.get(AttributeAttr.ENTITY_TYPE);
        Pair<Attribute, Collection<?>> condition = new ImmutablePair<>(entityTypeAttr, Collections.singleton(modelOp.type));
        List<Entity> attrEntities = modelDatabase.select(Collections.emptyList(),
                                                         modelModel.getEntityType(ModelType.ATTRIBUTE),
                                                         Collections.singleton(condition),
                                                         null);

        List<DataOp> dataOps = new ArrayList<>(attrEntities.size());
        for (Entity attrEntity : attrEntities) {
            dataOps.add(new DataOpDelete(attrEntity.id));
        }

        PatchData patch = new PatchData(dataOps, Collections.emptyList(), null, null, -1, -1);
        modelDatabase.apply(patch);
    }

    @Override
    public void appendAttribute(ModelOpAppendAttribute modelOp) throws Exception {
        DataOp dataOp = toDataOp(modelOp, modelOp.attr, false);
        modelDatabase.apply(dataOp);
    }

    @Override
    public void deleteAttribute(ModelOpDeleteAttribute modelOp) throws Exception {
        ReferenceInternal id = new ReferenceString(ModelType.ATTRIBUTE, modelOp.type + TYPE_ATTR_SEP + modelOp.attribName);
        DataOp dataOp = new DataOpDelete(id);
        modelDatabase.apply(dataOp);
    }

    public void load(Model entityModel) throws Exception {
        EntityType attributeType = modelModel.getEntityType(ModelType.ATTRIBUTE);
        List<Attribute> selectAttrs = Arrays.asList(AttributeAttr.ENTITY_TYPE,
                                                    AttributeAttr.ATTR_NAME,
                                                    AttributeAttr.IDENTITY,
                                                    AttributeAttr.VALUE_TYPE,
                                                    AttributeAttr.INITIAL)
            .stream()
            .map((name) -> attributeType.attrs.get(name))
            .collect(Collectors.toList());

        List<Entity> entities = modelDatabase.select(selectAttrs, attributeType, null, null);

        Map<String, ModelOp.Attribute> identities = new HashMap<>();
        Map<String, List<ModelOp.Attribute>> attributes = new HashMap<>();
        for (Entity entity : entities) {
            String entityType = (String) entity.attrs.get(AttributeAttr.ENTITY_TYPE);
            ModelOp.Attribute attribute = toAttribute(entity);
            if ((Boolean) entity.attrs.get(AttributeAttr.IDENTITY)) {
                identities.put(entityType, attribute);
            } else {
                List<ModelOp.Attribute> entityAttrs = attributes.computeIfAbsent(entityType, (k) -> new ArrayList<>());
                entityAttrs.add(attribute);
            }
        }

        for (Map.Entry<String, ModelOp.Attribute> entry : identities.entrySet()) {
            ModelOpCreateType createOp = new ModelOpCreateType(entry.getKey(),
                                                               entry.getValue(),
                                                               attributes.get(entry.getKey()));

            entityModel.createType(createOp);
        }
    }

    public PatchData toPatchData(ModelOpCreateType modelOp) throws Exception {
        List<DataOp> dataOps = new ArrayList<>(modelOp.attrs.size());

        dataOps.add(toDataOp(modelOp, modelOp.identity, true));
        for (ModelOp.Attribute attr : modelOp.attrs) {
            dataOps.add(toDataOp(modelOp, attr, false));
        }
        return new PatchData(dataOps, Collections.emptyList(), null, null, -1, -1);
    }

    private DataOpInsert toDataOp(ModelOp modelOp, ModelOp.Attribute attr, boolean identity) throws Exception {
        ReferenceInternal id = new ReferenceString(ModelType.ATTRIBUTE, modelOp.type + TYPE_ATTR_SEP + attr.name);
        Map<String, Object> attrs = new HashMap<>();
        attrs.put(AttributeAttr.ENTITY_TYPE, modelOp.type);
        attrs.put(AttributeAttr.ATTR_NAME, attr.name);
        attrs.put(AttributeAttr.IDENTITY, identity);
        attrs.put(AttributeAttr.VALUE_TYPE, toStringValue(attr.type));
        attrs.put(AttributeAttr.INITIAL, toStringValue(attr.type, attr.initial));

        return new DataOpInsert(id, attrs);
    }

    private ModelOp.Attribute toAttribute(Entity entity) throws Exception {
        String name = (String) entity.attrs.get(AttributeAttr.ATTR_NAME);
        ValueType valuetype = toValueType((String) entity.attrs.get(AttributeAttr.VALUE_TYPE));
        Object initial = toValue(valuetype, (String) entity.attrs.get(AttributeAttr.INITIAL));

        return new ModelOp.Attribute(name,
                                     valuetype,
                                     initial);
    }

    String toStringValue(ValueType type) {
        if (type instanceof PrimitiveType) {
            return Value.PRIMITIVE
                   + VALUE_TYPE_SEP
                   + ((PrimitiveType) type).name();
        }
        if (type instanceof ReferenceType) {
            return Value.REFERENCE
                   + VALUE_TYPE_SEP
                   + ((ReferenceType) type).storageType.name()
                   + VALUE_TYPE_SEP
                   + ((ReferenceType) type).entityType;
        }
        throw new RuntimeException("Unsupported Value Type: " + type);
    }

    ValueType toValueType(String stringValue) {
        String[] parts = StringUtils.split(stringValue, VALUE_TYPE_SEP);
        if (Value.PRIMITIVE.equals(parts[0])) {
            return PrimitiveType.valueOf(parts[1]);
        }
        if (Value.REFERENCE.equals(parts[0])) {
            return new ReferenceType(parts[2], PrimitiveType.valueOf(parts[1]));
        }
        throw new RuntimeException("Unsupported Value Type: " + stringValue);
    }

    String toStringValue(ValueType type, Object value) {
        if (null == value)
            return null;

        if (type instanceof ReferenceType) {
            return ((ReferenceInternal) value).stringValue;
        }
        if (type instanceof PrimitiveType) {
            switch ((PrimitiveType) type) {
                case BOOLEAN:
                    return ((Boolean) value).toString();
                case DOUBLE:
                    return Double.toString(((Number) value).doubleValue());
                case INTEGER:
                    return Long.toString(((Number) value).longValue());
                case REFERENCE_EXTERNAL:
                    return ((Reference) value).stringValue;
                case STRING:
                    return Objects.toString(value);
                case TEXT:
                    return Objects.toString(value);
                case TIMESTAMP:
                    return DATE_FORMAT.format(value);
            }
        }
        throw new RuntimeException("Unsupported Value Type: " + type);
    }

    Object toValue(ValueType type, String stringValue) throws Exception {
        if (null == stringValue)
            return null;

        if (type instanceof ReferenceType) {
            return ReferenceInternal.referenceFromString((ReferenceType) type, stringValue);
        }

        if (type instanceof PrimitiveType) {
            switch ((PrimitiveType) type) {
                case BOOLEAN:
                    return Boolean.valueOf(stringValue);
                case DOUBLE:
                    return Double.valueOf(stringValue);
                case INTEGER:
                    return Long.valueOf(stringValue);
                case REFERENCE_EXTERNAL:
                    return new ReferenceExternal(stringValue);
                case STRING:
                    return stringValue;
                case TEXT:
                    return stringValue;
                case TIMESTAMP:
                    return DATE_FORMAT.parse(stringValue);
            }
        }
        throw new RuntimeException("Unsupported Value Type: " + type);
    }

}
