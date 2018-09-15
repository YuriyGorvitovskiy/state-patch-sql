package org.state.patch.sql.patch;

import java.util.Date;

import org.state.patch.sql.data.ReferenceExternal;
import org.state.patch.sql.data.Traced;

public abstract class Patch extends Traced {

    protected Patch(ReferenceExternal modifiedBy, Date modifiedAt, long modifiedEventId, long modifiedPatchId) {
        super(modifiedBy, modifiedAt, modifiedEventId, modifiedPatchId);
    }

}
