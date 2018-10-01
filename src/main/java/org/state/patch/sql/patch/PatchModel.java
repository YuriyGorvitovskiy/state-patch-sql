package org.state.patch.sql.patch;

import java.util.Date;
import java.util.List;

import org.state.patch.sql.data.ReferenceExternal;
import org.state.patch.sql.model.op.ModelOp;

public class PatchModel extends Patch {

    public final List<ModelOp> operations;

    public PatchModel(List<ModelOp> operations,
                      List<String> targetIds,
                      ReferenceExternal modifiedBy,
                      Date modifiedAt,
                      long modifiedEventId,
                      long modifiedPatchId) {
        super(targetIds, modifiedBy, modifiedAt, modifiedEventId, modifiedPatchId);
        this.operations = operations;
    }

}
