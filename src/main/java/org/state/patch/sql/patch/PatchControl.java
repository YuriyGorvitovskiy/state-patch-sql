package org.state.patch.sql.patch;

import java.util.Date;
import java.util.List;

import org.state.patch.sql.control.op.ControlOp;
import org.state.patch.sql.data.ReferenceExternal;

public class PatchControl extends Patch {

    public final List<ControlOp> operations;

    public PatchControl(List<ControlOp> operations,
                        ReferenceExternal modifiedBy,
                        Date modifiedAt,
                        long modifiedEventId,
                        long modifiedPatchId) {
        super(modifiedBy, modifiedAt, modifiedEventId, modifiedPatchId);
        this.operations = operations;
    }

}
