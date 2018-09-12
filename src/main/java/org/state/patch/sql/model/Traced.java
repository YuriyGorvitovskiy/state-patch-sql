package org.state.patch.sql.model;

import java.util.Date;

public abstract class Traced {

    public final ReferenceExternal modifiedBy;
    public final Date              modifiedAt;
    public final long              modifiedEventId;
    public final long              modifiedPatchId;

    protected Traced(ReferenceExternal modifiedBy, Date modifiedAt, long modifiedEventId, long modifiedPatchId) {
        this.modifiedBy = modifiedBy;
        this.modifiedAt = modifiedAt;
        this.modifiedEventId = modifiedEventId;
        this.modifiedPatchId = modifiedPatchId;
    }
}
