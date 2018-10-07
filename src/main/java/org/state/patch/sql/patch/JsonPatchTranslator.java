package org.state.patch.sql.patch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.state.patch.sql.control.op.ControlOp;
import org.state.patch.sql.control.op.ControlOpBackup;
import org.state.patch.sql.control.op.ControlOpPing;
import org.state.patch.sql.control.op.ControlOpSuspend;
import org.state.patch.sql.data.Reference;
import org.state.patch.sql.data.ReferenceExternal;
import org.state.patch.sql.data.ReferenceInternal;
import org.state.patch.sql.data.ReferenceString;
import org.state.patch.sql.data.op.DataOp;
import org.state.patch.sql.data.op.DataOpDelete;
import org.state.patch.sql.data.op.DataOpInsert;
import org.state.patch.sql.data.op.DataOpUpdate;
import org.state.patch.sql.model.Attribute;
import org.state.patch.sql.model.EntityType;
import org.state.patch.sql.model.Model;
import org.state.patch.sql.model.PrimitiveType;
import org.state.patch.sql.model.ReferenceType;
import org.state.patch.sql.model.ValueType;
import org.state.patch.sql.model.op.ModelOp;
import org.state.patch.sql.model.op.ModelOpAppendAttribute;
import org.state.patch.sql.model.op.ModelOpCreateType;
import org.state.patch.sql.model.op.ModelOpDeleteAttribute;
import org.state.patch.sql.model.op.ModelOpDeleteType;
import org.state.patch.sql.patch.v1.JsonControlOp;
import org.state.patch.sql.patch.v1.JsonControlOpBackup;
import org.state.patch.sql.patch.v1.JsonControlOpPing;
import org.state.patch.sql.patch.v1.JsonControlOpSuspend;
import org.state.patch.sql.patch.v1.JsonDataOp;
import org.state.patch.sql.patch.v1.JsonDataOpDelete;
import org.state.patch.sql.patch.v1.JsonDataOpInsert;
import org.state.patch.sql.patch.v1.JsonDataOpUpdate;
import org.state.patch.sql.patch.v1.JsonModelAttribute;
import org.state.patch.sql.patch.v1.JsonModelOp;
import org.state.patch.sql.patch.v1.JsonModelOpAppendAttr;
import org.state.patch.sql.patch.v1.JsonModelOpCreateType;
import org.state.patch.sql.patch.v1.JsonModelOpDeleteAttr;
import org.state.patch.sql.patch.v1.JsonModelOpDeleteType;
import org.state.patch.sql.patch.v1.JsonPatchControl_v1;
import org.state.patch.sql.patch.v1.JsonPatchData_v1;
import org.state.patch.sql.patch.v1.JsonPatchModel_v1;
import org.state.patch.sql.translator.JsonTranslator;
import org.state.patch.sql.util.Json;

public class JsonPatchTranslator implements JsonTranslator<Patch, JsonPatch> {

    final Model model;

    public JsonPatchTranslator(Model model) {
        this.model = model;
    }

    @Override
    public Class<Patch> getEntityClass() {
        return Patch.class;
    }

    @Override
    public Class<JsonPatch> getJsonClass() {
        return JsonPatch.class;
    }

    @Override
    public Patch fromJson(JsonPatch patch) {
        if (patch instanceof JsonPatchData_v1) {
            return fromJson((JsonPatchData_v1) patch);
        }
        if (patch instanceof JsonPatchModel_v1) {
            return fromJson((JsonPatchModel_v1) patch);
        }
        if (patch instanceof JsonPatchControl_v1) {
            return fromJson((JsonPatchControl_v1) patch);
        }
        throw new RuntimeException("Unknown patch: " + patch);
    }

    @Override
    public JsonPatch toJson(Patch patch) {
        if (patch instanceof PatchData) {
            return toJson((PatchData) patch);
        }
        if (patch instanceof PatchModel) {
            return toJson((PatchModel) patch);
        }
        if (patch instanceof PatchControl) {
            return toJson((PatchControl) patch);
        }
        throw new RuntimeException("Unknown patch: " + patch);
    }

    PatchData fromJson(JsonPatchData_v1 patch) {
        ReferenceExternal eventBy = new ReferenceExternal(patch.event_by);
        Date eventAt = patch.event_at;
        long eventId = patch.event_id;
        long patchId = patch.message_id;

        List<String> targetIds = new ArrayList<>(patch.target_ids);

        List<DataOp> ops = new ArrayList<>(patch.ops.size());
        for (JsonDataOp jsonOp : patch.ops) {
            ops.add(fromJson(jsonOp));
        }

        return new PatchData(Collections.unmodifiableList(ops),
                             Collections.unmodifiableList(targetIds),
                             eventBy,
                             eventAt,
                             eventId,
                             patchId);
    }

    JsonPatchData_v1 toJson(PatchData patch) {
        JsonPatchData_v1 json = new JsonPatchData_v1();
        json.target_ids.addAll(patch.targetIds);
        json.message_id = patch.modifiedPatchId;
        json.event_id = patch.modifiedEventId;
        json.event_by = patch.modifiedBy.toString();
        json.event_at = patch.modifiedAt;

        for (DataOp op : patch.operations) {
            json.ops.add(toJson(op));
        }

        return json;
    }

    PatchModel fromJson(JsonPatchModel_v1 patch) {
        ReferenceExternal eventBy = new ReferenceExternal(patch.event_by);
        Date eventAt = patch.event_at;
        long eventId = patch.event_id;
        long patchId = patch.message_id;

        List<String> targetIds = (null == patch.target_ids) ? Collections.emptyList()
                                                            : new ArrayList<>(patch.target_ids);

        List<ModelOp> ops = new ArrayList<>(patch.ops.size());
        for (JsonModelOp jsonOp : patch.ops) {
            ops.add(fromJson(jsonOp));
        }

        return new PatchModel(Collections.unmodifiableList(ops),
                              Collections.unmodifiableList(targetIds),
                              eventBy,
                              eventAt,
                              eventId,
                              patchId);
    }

    JsonPatchModel_v1 toJson(PatchModel patch) {
        JsonPatchModel_v1 json = new JsonPatchModel_v1();
        json.target_ids.addAll(patch.targetIds);
        json.message_id = patch.modifiedPatchId;
        json.event_id = patch.modifiedEventId;
        json.event_by = patch.modifiedBy.toString();
        json.event_at = patch.modifiedAt;

        for (ModelOp op : patch.operations) {
            json.ops.add(toJson(op));
        }

        return json;
    }

    PatchControl fromJson(JsonPatchControl_v1 patch) {
        ReferenceExternal eventBy = new ReferenceExternal(patch.event_by);
        Date eventAt = patch.event_at;
        long eventId = patch.event_id;
        long patchId = patch.message_id;

        List<String> targetIds = new ArrayList<>(patch.target_ids);

        List<ControlOp> ops = new ArrayList<>(patch.ops.size());
        for (JsonControlOp jsonOp : patch.ops) {
            ops.add(fromJson(jsonOp));
        }

        return new PatchControl(Collections.unmodifiableList(ops),
                                Collections.unmodifiableList(targetIds),
                                eventBy,
                                eventAt,
                                eventId,
                                patchId);
    }

    JsonPatchControl_v1 toJson(PatchControl patch) {
        JsonPatchControl_v1 json = new JsonPatchControl_v1();
        json.target_ids.addAll(patch.targetIds);
        json.message_id = patch.modifiedPatchId;
        json.event_id = patch.modifiedEventId;
        json.event_by = patch.modifiedBy.toString();
        json.event_at = patch.modifiedAt;

        for (ControlOp op : patch.operations) {
            json.ops.add(toJson(op));
        }

        return json;
    }

    DataOp fromJson(JsonDataOp op) {
        if (op instanceof JsonDataOpUpdate) {
            return fromJson((JsonDataOpUpdate) op);
        }
        if (op instanceof JsonDataOpInsert) {
            return fromJson((JsonDataOpInsert) op);
        }
        if (op instanceof JsonDataOpDelete) {
            return fromJson((JsonDataOpDelete) op);
        }
        throw new RuntimeException("Unknown data operation: " + op);
    }

    JsonDataOp toJson(DataOp op) {
        if (op instanceof DataOpUpdate) {
            return toJson((DataOpUpdate) op);
        }
        if (op instanceof DataOpInsert) {
            return toJson((DataOpInsert) op);
        }
        if (op instanceof DataOpDelete) {
            return toJson((DataOpDelete) op);
        }
        throw new RuntimeException("Unknown data operation: " + op);
    }

    ModelOp fromJson(JsonModelOp op) {
        if (op instanceof JsonModelOpCreateType) {
            return fromJson((JsonModelOpCreateType) op);
        }
        if (op instanceof JsonModelOpAppendAttr) {
            return fromJson((JsonModelOpAppendAttr) op);
        }
        if (op instanceof JsonModelOpDeleteAttr) {
            return fromJson((JsonModelOpDeleteAttr) op);
        }
        if (op instanceof JsonModelOpDeleteType) {
            return fromJson((JsonModelOpDeleteType) op);
        }
        throw new RuntimeException("Unknown model operation: " + op);
    }

    JsonModelOp toJson(ModelOp op) {
        if (op instanceof ModelOpCreateType) {
            return toJson((ModelOpCreateType) op);
        }
        if (op instanceof ModelOpAppendAttribute) {
            return toJson((ModelOpAppendAttribute) op);
        }
        if (op instanceof ModelOpDeleteAttribute) {
            return toJson((ModelOpDeleteAttribute) op);
        }
        if (op instanceof ModelOpDeleteType) {
            return toJson((ModelOpDeleteType) op);
        }
        throw new RuntimeException("Unknown model operation: " + op);
    }

    ControlOp fromJson(JsonControlOp op) {
        if (op instanceof JsonControlOpPing) {
            return fromJson((JsonControlOpPing) op);
        }
        if (op instanceof JsonControlOpBackup) {
            return fromJson((JsonControlOpBackup) op);
        }
        if (op instanceof JsonControlOpSuspend) {
            return fromJson((JsonControlOpSuspend) op);
        }
        throw new RuntimeException("Unknown control operation: " + op);
    }

    JsonControlOp toJson(ControlOp op) {
        if (op instanceof ControlOpPing) {
            return toJson((ControlOpPing) op);
        }
        if (op instanceof ControlOpBackup) {
            return toJson((ControlOpBackup) op);
        }
        if (op instanceof ControlOpSuspend) {
            return toJson((ControlOpSuspend) op);
        }
        throw new RuntimeException("Unknown control operation: " + op);
    }

    DataOpUpdate fromJson(JsonDataOpUpdate op) {
        ReferenceInternal entityId = entityIdFromJson(op.entity_id);
        Map<String, Object> attrs = fromJson(op.attrs, entityId.type);

        return new DataOpUpdate(entityId, attrs);
    }

    JsonDataOpUpdate toJson(DataOpUpdate op) {
        JsonDataOpUpdate json = new JsonDataOpUpdate();
        json.entity_id = referenceToJson(op.id);

        EntityType entityType = model.getEntityType(op.id.type);
        for (Map.Entry<String, Object> attr : op.attrs.entrySet()) {
            String attrName = attr.getKey();
            ValueType attrType = getAttributeType(entityType, attrName);
            Object jsonValue = toJson(attrType, attr.getValue());
            json.attrs.put(attrName, jsonValue);
        }
        return json;
    }

    DataOpInsert fromJson(JsonDataOpInsert op) {
        ReferenceInternal entityId = entityIdFromJson(op.entity_id);
        Map<String, Object> attrs = fromJson(op.attrs, entityId.type);

        return new DataOpInsert(entityId, attrs);
    }

    JsonDataOpInsert toJson(DataOpInsert op) {
        JsonDataOpInsert json = new JsonDataOpInsert();
        json.entity_id = referenceToJson(op.id);

        EntityType entityType = model.getEntityType(op.id.type);
        for (Map.Entry<String, Object> attr : op.attrs.entrySet()) {
            String attrName = attr.getKey();
            ValueType attrType = getAttributeType(entityType, attrName);
            Object jsonValue = toJson(attrType, attr.getValue());
            json.attrs.put(attrName, jsonValue);
        }
        return json;
    }

    DataOpDelete fromJson(JsonDataOpDelete op) {
        ReferenceInternal entityId = entityIdFromJson(op.entity_id);
        return new DataOpDelete(entityId);
    }

    JsonDataOpDelete toJson(DataOpDelete op) {
        JsonDataOpDelete json = new JsonDataOpDelete();
        json.entity_id = referenceToJson(op.id);
        return json;
    }

    ModelOpCreateType fromJson(JsonModelOpCreateType op) {
        ModelOp.Attribute identity = fromJson(op.id);

        List<ModelOp.Attribute> attrs = new ArrayList<>(op.attrs.size());
        for (JsonModelAttribute attr : op.attrs) {
            attrs.add(fromJson(attr));
        }

        return new ModelOpCreateType(op.entity_type,
                                     identity,
                                     Collections.unmodifiableList(attrs));
    }

    JsonModelOpCreateType toJson(ModelOpCreateType op) {
        JsonModelOpCreateType json = new JsonModelOpCreateType();
        json.entity_type = op.type;
        json.id = toJson(op.identity);

        for (ModelOp.Attribute attr : op.attrs) {
            json.attrs.add(toJson(attr));
        }

        return json;
    }

    ModelOpAppendAttribute fromJson(JsonModelOpAppendAttr op) {
        ModelOp.Attribute attr = fromJson(op.attr);
        return new ModelOpAppendAttribute(op.entity_type, attr);
    }

    JsonModelOpAppendAttr toJson(ModelOpAppendAttribute op) {
        JsonModelOpAppendAttr json = new JsonModelOpAppendAttr();
        json.entity_type = op.type;
        json.attr = toJson(op.attr);
        return json;
    }

    ModelOpDeleteAttribute fromJson(JsonModelOpDeleteAttr op) {
        return new ModelOpDeleteAttribute(op.entity_type, op.attr_name);
    }

    JsonModelOpDeleteAttr toJson(ModelOpDeleteAttribute op) {
        JsonModelOpDeleteAttr json = new JsonModelOpDeleteAttr();
        json.entity_type = op.type;
        json.attr_name = op.attribName;
        return json;
    }

    ModelOpDeleteType fromJson(JsonModelOpDeleteType op) {
        return new ModelOpDeleteType(op.entity_type);
    }

    JsonModelOpDeleteType toJson(ModelOpDeleteType op) {
        JsonModelOpDeleteType json = new JsonModelOpDeleteType();
        json.entity_type = op.type;
        return json;
    }

    ControlOpPing fromJson(JsonControlOpPing op) {
        return new ControlOpPing();
    }

    JsonControlOpPing toJson(ControlOpPing op) {
        JsonControlOpPing json = new JsonControlOpPing();
        return json;
    }

    ControlOpBackup fromJson(JsonControlOpBackup op) {
        return new ControlOpBackup(op.incremental, op.backup_file);
    }

    JsonControlOpBackup toJson(ControlOpBackup op) {
        JsonControlOpBackup json = new JsonControlOpBackup();
        json.incremental = op.incremental;
        json.backup_file = op.backupFile;
        return json;
    }

    ControlOpSuspend fromJson(JsonControlOpSuspend op) {
        return new ControlOpSuspend(op.shutdown);
    }

    JsonControlOpSuspend toJson(ControlOpSuspend op) {
        JsonControlOpSuspend json = new JsonControlOpSuspend();
        json.shutdown = op.shutdown;
        return json;
    }

    ModelOp.Attribute fromJson(JsonModelAttribute attr) {
        ValueType type = valueTypeFromJson(attr.type);
        Object intial = fromJson(type, attr.initial);
        return new ModelOp.Attribute(attr.name, type, intial);
    }

    JsonModelAttribute toJson(ModelOp.Attribute attr) {
        JsonModelAttribute json = new JsonModelAttribute();
        json.name = attr.name;
        json.type = valueTypeToJson(attr.type);
        json.initial = toJson(attr.type, attr.initial);
        return json;
    }

    ValueType valueTypeFromJson(String type) {
        switch (type) {
            case "boolean":
                return PrimitiveType.BOOLEAN;
            case "integer":
                return PrimitiveType.INTEGER;
            case "double":
                return PrimitiveType.DOUBLE;
            case "string":
                return PrimitiveType.STRING;
            case "text":
                return PrimitiveType.TEXT;
            case "timestamp":
                return PrimitiveType.TIMESTAMP;
            case "refext":
                return PrimitiveType.REFERENCE_EXTERNAL;
        }
        if (type.startsWith("ref-integer:")) {
            return new ReferenceType(type.substring("ref-integer:".length()), PrimitiveType.INTEGER);
        }
        if (type.startsWith("ref-string:")) {
            return new ReferenceType(type.substring("ref-string:".length()), PrimitiveType.STRING);
        }
        throw new RuntimeException("Unknown value type: " + type);
    }

    String valueTypeToJson(ValueType type) {
        if (type instanceof PrimitiveType) {
            switch ((PrimitiveType) type) {
                case BOOLEAN:
                    return "boolean";
                case INTEGER:
                    return "integer";
                case DOUBLE:
                    return "double";
                case STRING:
                    return "string";
                case TEXT:
                    return "text";
                case TIMESTAMP:
                    return "timestamp";
                case REFERENCE_EXTERNAL:
                    return "refext";
            }
        }
        if (type instanceof ReferenceType) {
            ReferenceType refType = (ReferenceType) type;
            if (PrimitiveType.INTEGER == refType.storageType) {
                return "ref-integer:" + refType.entityType;
            }
            if (PrimitiveType.STRING == refType.storageType) {
                return "ref-string:" + refType.entityType;
            }
        }
        throw new RuntimeException("Unknown value type: " + type);
    }

    Map<String, Object> fromJson(Map<String, Object> jsonAttrs, String entityTypeName) {
        EntityType entityType = model.getEntityType(entityTypeName);
        if (null == entityType) {
            return new HashMap<>(jsonAttrs);
        }

        HashMap<String, Object> translated = new HashMap<>();
        for (Map.Entry<String, Object> jsonAttr : jsonAttrs.entrySet()) {
            ValueType attrType = getAttributeType(entityType, jsonAttr.getKey());
            translated.put(jsonAttr.getKey(), fromJson(attrType, jsonAttr.getValue()));
        }
        return translated;

    }

    Object fromJson(ValueType type, Object json) {
        if (null == json || null == type) {
            return json;
        }

        if (type instanceof PrimitiveType) {
            switch ((PrimitiveType) type) {
                case BOOLEAN:
                    return booleanFromJson(json);
                case DOUBLE:
                    return doubleFromJson(json);
                case INTEGER:
                    return integerFromJson(json);
                case REFERENCE_EXTERNAL:
                    return new ReferenceExternal(Objects.toString(json));
                case STRING:
                case TEXT:
                    return Objects.toString(json);
                case TIMESTAMP:
                    return dateFromJson(json);
            }
        }
        if (type instanceof ReferenceType) {
            return ReferenceInternal.referenceFromString((ReferenceType) type, Objects.toString(json));
        }
        throw new RuntimeException("Unknown value type: " + type);
    }

    Object toJson(ValueType type, Object java) {
        if (null == type || null == java) {
            return java;
        }

        if (type instanceof PrimitiveType) {
            switch ((PrimitiveType) type) {
                case BOOLEAN:
                    return booleanToJson(java);
                case DOUBLE:
                case INTEGER:
                    return numberToJson(java);
                case REFERENCE_EXTERNAL:
                    return referenceToJson(java);
                case STRING:
                case TEXT:
                    return Objects.toString(java);
                case TIMESTAMP:
                    return dateToJson(java);
            }
        }
        if (type instanceof ReferenceType) {
            return referenceToJson(java);
        }
        throw new RuntimeException("Unknown value type: " + type);
    }

    Boolean booleanFromJson(Object json) {
        if (json instanceof Boolean) {
            return ((Boolean) json);
        }
        if (json instanceof Number) {
            return ((Number) json).doubleValue() != 0.0;
        }
        return Boolean.valueOf(Objects.toString(json));
    }

    Boolean booleanToJson(Object java) {
        return (Boolean) java;
    }

    Long integerFromJson(Object json) {
        if (json instanceof Number) {
            return ((Number) json).longValue();
        }
        return Long.parseLong(Objects.toString(json));
    }

    Double doubleFromJson(Object json) {
        if (json instanceof Number) {
            return ((Number) json).doubleValue();
        }
        return Double.parseDouble(Objects.toString(json));
    }

    Number numberToJson(Object java) {
        return (Number) java;
    }

    Date dateFromJson(Object json) {
        if (json instanceof Number) {
            return new Date(((Number) json).longValue());
        }
        return Json.parseDate(Objects.toString(json));
    }

    String dateToJson(Object java) {
        return Json.formatDate((Date) java);
    }

    ReferenceInternal entityIdFromJson(Object json) {
        String jsonString = Objects.toString(json);
        String[] parts = jsonString.split(Reference.SEPARATOR);
        String entityTypeName = parts[parts.length - 2];
        String storageId = parts[parts.length - 1];

        EntityType entityType = model.getEntityType(entityTypeName);
        if (null == entityType) {
            return new ReferenceString(entityTypeName, storageId);
        }

        ReferenceType refType = (ReferenceType) entityType.identity.type;
        return ReferenceInternal.referenceFromString(refType, storageId);
    }

    String referenceToJson(Object java) {
        return ((Reference) java).stringValue;
    }

    ValueType getAttributeType(EntityType entityType, String attrName) {
        if (null == entityType) {
            return null;
        }
        Attribute attribute = entityType.attrs.get(attrName);
        if (null == attribute) {
            return null;
        }
        return attribute.type;
    }

}
