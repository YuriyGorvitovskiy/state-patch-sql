package org.state.patch.sql.model.op;

import java.util.Date;

import org.state.patch.sql.data.ReferenceExternal;

public class ModelOpDeleteAttribute extends ModelOp {

    public final String attribName;

    public ModelOpDeleteAttribute(String type,
                                  String attribName,
                                  ReferenceExternal issuedBy,
                                  Date issuedAt,
                                  long eventId,
                                  long patchId) {
        super(type, issuedBy, issuedAt, eventId, patchId);
        this.attribName = attribName;
    }
}
