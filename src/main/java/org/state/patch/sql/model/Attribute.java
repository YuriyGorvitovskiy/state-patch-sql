package org.state.patch.sql.model;

import java.util.Date;

public class Attribute extends Traced {

    public final String    attribName;
    public final String    columnName;
    public final ValueType valueType;
    public final String    valueReferenceType;
    public final Object    valueInitial;

    public Attribute(
            String attribName,
            String columnName,
            ValueType valueType,
            String valueReferenceType,
            Object valueInitial,
            ReferenceExternal modifiedBy,
            Date modifiedAt,
            long modifiedEventId,
            long modifiedPatchId) {

        super(modifiedBy, modifiedAt, modifiedEventId, modifiedPatchId);

        this.attribName = attribName;
        this.columnName = columnName;
        this.valueType = valueType;
        this.valueReferenceType = valueReferenceType;
        this.valueInitial = valueInitial;
    }

    @Override
    public String toString() {
        return "Attribute (" + attribName + ")";
    }
}
