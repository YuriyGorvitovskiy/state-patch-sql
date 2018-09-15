package org.state.patch.sql.data.op;

import java.util.Date;

import org.state.patch.sql.data.Reference;
import org.state.patch.sql.data.ReferenceExternal;
import org.state.patch.sql.data.Traced;

public abstract class DataOp extends Traced {

    public final Reference id;

    protected DataOp(Reference id,
                     ReferenceExternal modifiedBy,
                     Date modifiedAt,
                     long modifiedEventId,
                     long modifiedPatchId) {
        super(modifiedBy, modifiedAt, modifiedEventId, modifiedPatchId);
        this.id = id;
    }
}
