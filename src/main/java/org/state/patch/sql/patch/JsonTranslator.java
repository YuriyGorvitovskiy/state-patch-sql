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
import org.state.patch.sql.control.op.ControlOpSuspend;
import org.state.patch.sql.data.Reference;
import org.state.patch.sql.data.ReferenceExternal;
import org.state.patch.sql.data.ReferenceInternal;
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
import org.state.patch.sql.patch.v1.JsonPatchControl;
import org.state.patch.sql.patch.v1.JsonPatchData;
import org.state.patch.sql.patch.v1.JsonPatchModel;

import com.fasterxml.jackson.databind.util.StdDateFormat;

public class JsonTranslator {

    static final StdDateFormat DATE_FORMAT = new StdDateFormat();

    final Model model;

    public JsonTranslator(Model model) {
        this.model = model;
    }

    public Patch fromJson(JsonPatch patch) throws Exception {
        if (patch instanceof JsonPatchData) {
            return fromJson((JsonPatchData) patch);
        }
        if (patch instanceof JsonPatchModel) {
            return fromJson((JsonPatchModel) patch);
        }
        if (patch instanceof JsonPatchControl) {
            return fromJson((JsonPatchControl) patch);
        }
        throw new Exception("Unknown patch: " + patch);
    }

    private PatchData fromJson(JsonPatchData patch) throws Exception {
        ReferenceExternal eventBy = new ReferenceExternal(patch.event_by);
        Date eventAt = DATE_FORMAT.parse(patch.event_at);
        long eventId = patch.event_id;
        long patchId = patch.patch_id;

        List<DataOp> ops = new ArrayList<>(patch.ops.size());
        for (JsonDataOp jsonOp : patch.ops) {
            DataOp op = fromJson(jsonOp, eventBy, eventAt, eventId, patchId);
            if (null != op) {
                ops.add(op);
            }
        }

        return new PatchData(Collections.unmodifiableList(ops),
                             eventBy,
                             eventAt,
                             eventId,
                             patchId);
    }

    private PatchModel fromJson(JsonPatchModel patch) throws Exception {
        ReferenceExternal eventBy = new ReferenceExternal(patch.event_by);
        Date eventAt = DATE_FORMAT.parse(patch.event_at);
        long eventId = patch.event_id;
        long patchId = patch.patch_id;

        List<ModelOp> ops = new ArrayList<>(patch.ops.size());
        for (JsonModelOp jsonOp : patch.ops) {
            ModelOp op = fromJson(jsonOp, eventBy, eventAt, eventId, patchId);
            if (null != op) {
                ops.add(op);
            }
        }

        return new PatchModel(Collections.unmodifiableList(ops),
                              eventBy,
                              eventAt,
                              eventId,
                              patchId);
    }

    private PatchControl fromJson(JsonPatchControl patch) throws Exception {
        ReferenceExternal eventBy = new ReferenceExternal(patch.event_by);
        Date eventAt = DATE_FORMAT.parse(patch.event_at);
        long eventId = patch.event_id;
        long patchId = patch.patch_id;

        List<ControlOp> ops = new ArrayList<>(patch.ops.size());
        for (JsonControlOp jsonOp : patch.ops) {
            ControlOp op = fromJson(jsonOp, eventBy, eventAt, eventId, patchId);
            if (null != op) {
                ops.add(op);
            }
        }

        return new PatchControl(Collections.unmodifiableList(ops),
                                eventBy,
                                eventAt,
                                eventId,
                                patchId);
    }

    private DataOp fromJson(JsonDataOp op,
                            ReferenceExternal eventBy,
                            Date eventAt,
                            long eventId,
                            long patchId) throws Exception {
        if (op instanceof JsonDataOpUpdate) {
            return fromJson((JsonDataOpUpdate) op, eventBy, eventAt, eventId, patchId);
        }
        if (op instanceof JsonDataOpInsert) {
            return fromJson((JsonDataOpInsert) op, eventBy, eventAt, eventId, patchId);
        }
        if (op instanceof JsonDataOpDelete) {
            return fromJson((JsonDataOpDelete) op, eventBy, eventAt, eventId, patchId);
        }
        throw new Exception("Unknown data op: " + op);
    }

    private ModelOp fromJson(JsonModelOp op,
                             ReferenceExternal eventBy,
                             Date eventAt,
                             long eventId,
                             long patchId) throws Exception {
        if (op instanceof JsonModelOpCreateType) {
            return fromJson((JsonModelOpCreateType) op, eventBy, eventAt, eventId, patchId);
        }
        if (op instanceof JsonModelOpAppendAttr) {
            return fromJson((JsonModelOpAppendAttr) op, eventBy, eventAt, eventId, patchId);
        }
        if (op instanceof JsonModelOpDeleteAttr) {
            return fromJson((JsonModelOpDeleteAttr) op, eventBy, eventAt, eventId, patchId);
        }
        if (op instanceof JsonModelOpDeleteType) {
            return fromJson((JsonModelOpDeleteType) op, eventBy, eventAt, eventId, patchId);
        }
        throw new Exception("Unknown model op: " + op);
    }

    private ControlOp fromJson(JsonControlOp op,
                               ReferenceExternal eventBy,
                               Date eventAt,
                               long eventId,
                               long patchId) throws Exception {
        if (op instanceof JsonControlOpSuspend) {
            return fromJson((JsonControlOpSuspend) op, eventBy, eventAt, eventId, patchId);
        }
        if (op instanceof JsonControlOpBackup) {
            return fromJson((JsonControlOpBackup) op, eventBy, eventAt, eventId, patchId);
        }
        throw new Exception("Unknown control op: " + op);
    }

    private DataOpUpdate fromJson(JsonDataOpUpdate op,
                                  ReferenceExternal eventBy,
                                  Date eventAt,
                                  long eventId,
                                  long patchId) throws Exception {
        ReferenceInternal entityId = entityIdFromJson(op.entity_id);
        if (null == entityId) {
            // Skip operation for unmanaged Entity Type
            return null;
        }

        Map<String, Object> attrs = fromJson(op.attrs, entityId.type);

        return new DataOpUpdate(entityId,
                                attrs,
                                eventBy,
                                eventAt,
                                eventId,
                                patchId);
    }

    private DataOpInsert fromJson(JsonDataOpInsert op,
                                  ReferenceExternal eventBy,
                                  Date eventAt,
                                  long eventId,
                                  long patchId) throws Exception {
        ReferenceInternal entityId = entityIdFromJson(op.entity_id);
        if (null == entityId) {
            // Skip operation for unmanaged Entity Type
            return null;
        }

        Map<String, Object> attrs = fromJson(op.attrs, entityId.type);

        return new DataOpInsert(entityId,
                                attrs,
                                eventBy,
                                eventAt,
                                eventId,
                                patchId);
    }

    private DataOpDelete fromJson(JsonDataOpDelete op,
                                  ReferenceExternal eventBy,
                                  Date eventAt,
                                  long eventId,
                                  long patchId) throws Exception {
        ReferenceInternal entityId = entityIdFromJson(op.entity_id);
        if (null == entityId) {
            // Skip operation for unmanaged Entity Type
            return null;
        }
        return new DataOpDelete(entityId,
                                eventBy,
                                eventAt,
                                eventId,
                                patchId);
    }

    private ModelOpCreateType fromJson(JsonModelOpCreateType op,
                                       ReferenceExternal eventBy,
                                       Date eventAt,
                                       long eventId,
                                       long patchId) throws Exception {
        ModelOp.Attribute identity = fromJson(op.id);

        List<ModelOp.Attribute> attrs = new ArrayList<>(op.attrs.size());
        for (JsonModelAttribute attr : op.attrs) {
            attrs.add(fromJson(attr));
        }

        return new ModelOpCreateType(op.entity_type,
                                     identity,
                                     Collections.unmodifiableList(attrs),
                                     eventBy,
                                     eventAt,
                                     eventId,
                                     patchId);
    }

    private ModelOpAppendAttribute fromJson(JsonModelOpAppendAttr op,
                                            ReferenceExternal eventBy,
                                            Date eventAt,
                                            long eventId,
                                            long patchId) throws Exception {
        ModelOp.Attribute attr = fromJson(op.attr);
        return new ModelOpAppendAttribute(op.entity_type,
                                          attr,
                                          eventBy,
                                          eventAt,
                                          eventId,
                                          patchId);
    }

    private ModelOpDeleteAttribute fromJson(JsonModelOpDeleteAttr op,
                                            ReferenceExternal eventBy,
                                            Date eventAt,
                                            long eventId,
                                            long patchId) {
        return new ModelOpDeleteAttribute(op.entity_type,
                                          op.attr_name,
                                          eventBy,
                                          eventAt,
                                          eventId,
                                          patchId);
    }

    private ModelOpDeleteType fromJson(JsonModelOpDeleteType op,
                                       ReferenceExternal eventBy,
                                       Date eventAt,
                                       long eventId,
                                       long patchId) {
        return new ModelOpDeleteType(op.entity_type,
                                     eventBy,
                                     eventAt,
                                     eventId,
                                     patchId);
    }

    private ControlOpSuspend fromJson(JsonControlOpSuspend op,
                                      ReferenceExternal eventBy,
                                      Date eventAt,
                                      long eventId,
                                      long patchId) {
        return new ControlOpSuspend(op.shutdown,
                                    eventBy,
                                    eventAt,
                                    eventId,
                                    patchId);
    }

    private ControlOpBackup fromJson(JsonControlOpBackup op,
                                     ReferenceExternal eventBy,
                                     Date eventAt,
                                     long eventId,
                                     long patchId) {
        return new ControlOpBackup(op.incremental,
                                   op.backup_file,
                                   eventBy,
                                   eventAt,
                                   eventId,
                                   patchId);
    }

    private ModelOp.Attribute fromJson(JsonModelAttribute attr) throws Exception {
        ValueType type = fromJson(attr.type);
        Object intial = fromJson(type, attr.initial);
        return new ModelOp.Attribute(attr.name, type, intial);
    }

    private ValueType fromJson(String type) throws Exception {
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
        throw new Exception("Unknown value type: " + type);
    }

    private Map<String, Object> fromJson(Map<String, Object> jsonAttrs, String entityTypeName) throws Exception {
        EntityType entityType = model.getEntityType(entityTypeName);
        if (null == entityType) {
            return new HashMap<>(jsonAttrs);
        }

        HashMap<String, Object> translated = new HashMap<>();
        for (Map.Entry<String, Object> jsonAttr : jsonAttrs.entrySet()) {
            Attribute modelAttr = entityType.attrs.get(jsonAttr.getKey());

            // Skip unmanaged attributes
            if (null != modelAttr) {
                translated.put(jsonAttr.getKey(), fromJson(modelAttr.type, jsonAttr.getValue()));
            }
        }
        return translated;
    }

    private Object fromJson(ValueType type, Object json) throws Exception {
        if (null == json) {
            return null;
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
        } else if (type instanceof ReferenceType) {
            return ReferenceInternal.referenceFromObject((ReferenceType) type, json);
        }
        throw new Exception("Unknown value type: " + type);
    }

    private Boolean booleanFromJson(Object json) {
        if (null == json) {
            return null;
        }
        if (json instanceof Boolean) {
            return ((Boolean) json);
        }
        if (json instanceof Number) {
            return ((Number) json).doubleValue() != 0.0;
        }
        return Boolean.valueOf(Objects.toString(json));
    }

    private Long integerFromJson(Object json) {
        if (null == json) {
            return null;
        }
        if (json instanceof Number) {
            return ((Number) json).longValue();
        }
        return Long.parseLong(Objects.toString(json));
    }

    private Double doubleFromJson(Object json) {
        if (null == json) {
            return null;
        }
        if (json instanceof Number) {
            return ((Number) json).doubleValue();
        }
        return Double.parseDouble(Objects.toString(json));
    }

    private Date dateFromJson(Object json) throws Exception {
        if (null == json) {
            return null;
        }
        if (json instanceof Number) {
            return new Date(((Number) json).longValue());
        }
        return DATE_FORMAT.parse(Objects.toString(json));
    }

    private ReferenceInternal entityIdFromJson(Object json) throws Exception {
        String[] parts = Objects.toString(json).split(Reference.SEPARATOR);
        String entityTypeName = parts[parts.length - 2];
        String storageId = parts[parts.length - 1];

        EntityType entityType = model.getEntityType(entityTypeName);
        if (null == entityType) {
            return null;
        }

        ReferenceType refType = (ReferenceType) entityType.identity.type;
        return ReferenceInternal.referenceFromString(refType, storageId);
    }
}
