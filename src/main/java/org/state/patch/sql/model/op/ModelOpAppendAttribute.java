package org.state.patch.sql.model.op;

import java.util.Date;

import org.state.patch.sql.data.ReferenceExternal;

public class ModelOpAppendAttribute extends ModelOp {

    public final Attribute attr;

    public ModelOpAppendAttribute(String type,
                                  Attribute attr,
                                  ReferenceExternal issuedBy,
                                  Date issuedAt,
                                  long eventId,
                                  long patchId) {
        super(type, issuedBy, issuedAt, eventId, patchId);
        this.attr = attr;
    }
}
