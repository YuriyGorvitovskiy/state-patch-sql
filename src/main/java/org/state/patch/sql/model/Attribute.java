package org.state.patch.sql.model;

import java.util.Date;

public class Attribute extends Traced {

    public final String    name;
    public final ValueType type;
    public final Object    initial;

    public Attribute(String name,
                     ValueType type,
                     Object initial,
                     ReferenceExternal modifiedBy,
                     Date modifiedAt,
                     long modifiedEventId,
                     long modifiedPatchId) {

        super(modifiedBy, modifiedAt, modifiedEventId, modifiedPatchId);

        this.name = name;
        this.type = type;
        this.initial = initial;
    }

    @Override
    public String toString() {
        return "Attribute (" + name + ")";
    }
}
