package org.state.patch.sql.patch;

import org.state.patch.sql.data.op.DataOp;
import org.state.patch.sql.data.op.DataOpDelete;
import org.state.patch.sql.data.op.DataOpInsert;
import org.state.patch.sql.data.op.DataOpUpdate;

public interface PatchDataProcessor {

    public default void apply(PatchData dataPatch) throws Exception {
        for (DataOp operation : dataPatch.operations) {
            this.apply(operation);
        }
    }

    public default void apply(DataOp operation) throws Exception {
        if (operation instanceof DataOpInsert) {
            insert((DataOpInsert) operation);
        } else if (operation instanceof DataOpUpdate) {
            update((DataOpUpdate) operation);
        } else if (operation instanceof DataOpDelete) {
            delete((DataOpDelete) operation);
        } else {
            throw new RuntimeException("Unsupported Data Operation: " + operation);
        }
    }

    public void insert(DataOpInsert op) throws Exception;

    public void update(DataOpUpdate op) throws Exception;

    public void delete(DataOpDelete op) throws Exception;

}
