package org.state.patch.sql.control.op;

import java.util.Date;

import org.state.patch.sql.data.ReferenceExternal;

public class ControlOpSuspend extends ControlOp {
    public final boolean shutdown;

    public ControlOpSuspend(boolean shutdown,
                            ReferenceExternal modifiedBy,
                            Date modifiedAt,
                            long modifiedEventId,
                            long modifiedPatchId) {
        super(modifiedBy, modifiedAt, modifiedEventId, modifiedPatchId);
        this.shutdown = shutdown;
    }

}
