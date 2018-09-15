package org.state.patch.sql.model.op;

import java.util.Date;

import org.state.patch.sql.data.ReferenceExternal;

public class ModelOpDeleteType extends ModelOp {

    public ModelOpDeleteType(String type,
                             ReferenceExternal issuedBy,
                             Date issuedAt,
                             long eventId,
                             long patchId) {
        super(type, issuedBy, issuedAt, eventId, patchId);
    }
}
