package org.state.patch.sql.control.op;

public class ControlOpBackup extends ControlOp {
    public final boolean incremental;
    public final String  backupFile;

    public ControlOpBackup(boolean incremental,
                           String backupFile) {
        this.incremental = incremental;
        this.backupFile = backupFile;
    }
}
