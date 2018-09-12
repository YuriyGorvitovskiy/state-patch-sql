package org.state.patch.sql.model.op;

import java.util.Date;

import org.state.patch.sql.model.ReferenceExternal;

public abstract class ModelOpDeleteAttribute extends ModelOpAbstract {

    public final String attribName;

    public ModelOpDeleteAttribute(
            String type,
            String attribName,
            ReferenceExternal issuedBy,
            Date issuedAt,
            long eventId,
            long patchId) {
        super(type, issuedBy, issuedAt, eventId, patchId);
        this.attribName = attribName;
    }
}
