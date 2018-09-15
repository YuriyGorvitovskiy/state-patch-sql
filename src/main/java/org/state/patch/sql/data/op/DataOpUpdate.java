package org.state.patch.sql.data.op;

import java.util.Date;
import java.util.Map;

import org.state.patch.sql.data.ReferenceExternal;
import org.state.patch.sql.data.ReferenceInternal;

public class DataOpUpdate extends DataOp {
    public final Map<String, Object> attrs;

    public DataOpUpdate(ReferenceInternal id,
                        Map<String, Object> attrs,
                        ReferenceExternal modifiedBy,
                        Date modifiedAt,
                        long modifiedEventId,
                        long modifiedPatchId) {
        super(id, modifiedBy, modifiedAt, modifiedEventId, modifiedPatchId);
        this.attrs = attrs;
    }

}
