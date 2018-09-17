package org.state.patch.sql.data;

import java.util.Objects;

import org.state.patch.sql.model.PrimitiveType;
import org.state.patch.sql.model.ReferenceType;

public abstract class ReferenceInternal extends Reference {

    public final String type;

    public ReferenceInternal(String type, String complete) {
        super(complete);
        this.type = type;
    }

    public static ReferenceInternal referenceFromString(ReferenceType type, String stringValue) throws Exception {
        String[] parts = stringValue.split(Reference.SEPARATOR);
        String storageId = parts[parts.length - 1];

        return referenceFromString(type, storageId);
    }

    public static ReferenceInternal referenceFromObject(ReferenceType refType, Object storageId) throws Exception {
        if (PrimitiveType.INTEGER == refType.storageType) {
            if (storageId instanceof Number) {
                return new ReferenceInteger(refType.entityType, ((Number) storageId).longValue());
            }
            return new ReferenceInteger(refType.entityType, Long.parseLong(Objects.toString(storageId)));
        } else if (PrimitiveType.STRING == refType.storageType) {
            return new ReferenceString(refType.entityType, Objects.toString(storageId));
        }
        throw new Exception("Unknown reference storage type: " + refType.storageType);
    }

}
