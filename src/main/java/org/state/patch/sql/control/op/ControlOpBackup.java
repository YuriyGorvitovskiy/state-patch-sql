package org.state.patch.sql.control.op;

import java.util.Date;

import org.state.patch.sql.data.ReferenceExternal;

public class ControlOpBackup extends ControlOp {
    public final boolean incremental;
    public final String  backupFile;

    public ControlOpBackup(boolean incremental,
                           String backupFile,
                           ReferenceExternal modifiedBy,
                           Date modifiedAt,
                           long modifiedEventId,
                           long modifiedPatchId) {
        super(modifiedBy, modifiedAt, modifiedEventId, modifiedPatchId);
        this.incremental = incremental;
        this.backupFile = backupFile;
    }
}
