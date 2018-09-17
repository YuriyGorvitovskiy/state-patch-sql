package org.state.patch.sql.data.op;

import org.state.patch.sql.data.ReferenceInternal;

public abstract class DataOp {

    public final ReferenceInternal id;

    protected DataOp(ReferenceInternal id) {
        this.id = id;
    }
}
