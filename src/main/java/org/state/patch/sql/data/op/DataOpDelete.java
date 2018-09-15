package org.state.patch.sql.data.op;

import java.util.Date;

import org.state.patch.sql.data.Reference;
import org.state.patch.sql.data.ReferenceExternal;

public class DataOpDelete extends DataOp {
    public DataOpDelete(Reference id,
                        ReferenceExternal modifiedBy,
                        Date modifiedAt,
                        long modifiedEventId,
                        long modifiedPatchId) {
        super(id, modifiedBy, modifiedAt, modifiedEventId, modifiedPatchId);
    }
}
