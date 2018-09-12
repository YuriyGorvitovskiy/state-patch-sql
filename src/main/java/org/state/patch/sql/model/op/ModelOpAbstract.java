package org.state.patch.sql.model.op;

import java.util.Date;

import org.state.patch.sql.model.ReferenceExternal;
import org.state.patch.sql.model.ValueType;

public abstract class ModelOpAbstract {

    public static class Attribute {
        public final String    attribName;
        public final String    columnName;
        public final ValueType valueType;
        public final Object    valueInitial;

        public Attribute(String attribName,
                         String columnName,
                         ValueType valueType,
                         Object valueInitial) {
            this.attribName = attribName;
            this.columnName = columnName;
            this.valueType = valueType;
            this.valueInitial = valueInitial;
        }
    }

    public final String            type;
    public final ReferenceExternal issuedBy;
    public final Date              issuedAt;
    public final long              eventId;
    public final long              patchId;

    protected ModelOpAbstract(
                              String type,
                              ReferenceExternal issuedBy,
                              Date issuedAt,
                              long eventId,
                              long patchId) {
        this.type = type;
        this.issuedBy = issuedBy;
        this.issuedAt = issuedAt;
        this.eventId = eventId;
        this.patchId = patchId;
    }
}
