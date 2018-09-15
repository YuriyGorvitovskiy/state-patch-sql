package org.state.patch.sql.control.op;

import java.util.Date;

import org.state.patch.sql.data.ReferenceExternal;
import org.state.patch.sql.data.Traced;

public abstract class ControlOp extends Traced {

    protected ControlOp(ReferenceExternal modifiedBy, Date modifiedAt, long modifiedEventId, long modifiedPatchId) {
        super(modifiedBy, modifiedAt, modifiedEventId, modifiedPatchId);
    }
}
