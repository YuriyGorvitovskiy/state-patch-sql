package org.state.patch.sql.patch;

import java.util.Date;
import java.util.List;

import org.state.patch.sql.data.ReferenceExternal;
import org.state.patch.sql.data.Traced;

public abstract class Patch extends Traced {
    public final List<String> targetIds;

    protected Patch(List<String> targetIds,
                    ReferenceExternal modifiedBy,
                    Date modifiedAt,
                    long modifiedEventId,
                    long modifiedPatchId) {
        super(modifiedBy, modifiedAt, modifiedEventId, modifiedPatchId);
        this.targetIds = targetIds;
    }

}
