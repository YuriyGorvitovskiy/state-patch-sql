package org.state.patch.sql.data.op;

import java.util.Date;

import org.state.patch.sql.data.ReferenceExternal;
import org.state.patch.sql.data.ReferenceInternal;

public class DataOpDelete extends DataOp {
    public DataOpDelete(ReferenceInternal id,
                        ReferenceExternal modifiedBy,
                        Date modifiedAt,
                        long modifiedEventId,
                        long modifiedPatchId) {
        super(id, modifiedBy, modifiedAt, modifiedEventId, modifiedPatchId);
    }
}
