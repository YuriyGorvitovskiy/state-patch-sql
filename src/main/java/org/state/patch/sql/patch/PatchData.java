package org.state.patch.sql.patch;

import java.util.Date;
import java.util.List;

import org.state.patch.sql.data.ReferenceExternal;
import org.state.patch.sql.data.op.DataOp;

public class PatchData extends Patch {

    public final List<DataOp> operations;

    public PatchData(List<DataOp> operations,
                     ReferenceExternal modifiedBy,
                     Date modifiedAt,
                     long modifiedEventId,
                     long modifiedPatchId) {
        super(modifiedBy, modifiedAt, modifiedEventId, modifiedPatchId);
        this.operations = operations;
    }

}
