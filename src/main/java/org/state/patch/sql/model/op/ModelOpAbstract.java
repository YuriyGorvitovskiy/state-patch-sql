package org.state.patch.sql.model.op;

import java.util.Date;

import org.state.patch.sql.model.ReferenceExternal;
import org.state.patch.sql.model.ValueType;

public abstract class ModelOpAbstract {

    public static class Attribute {
        public final String    name;
        public final ValueType type;
        public final Object    initial;

        public Attribute(String attribName,
                         ValueType valueType,
                         Object valueInitial) {
            this.name = attribName;
            this.type = valueType;
            this.initial = valueInitial;
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
