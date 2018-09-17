package org.state.patch.sql.data.op;

import java.util.Map;

import org.state.patch.sql.data.ReferenceInternal;

public class DataOpUpdate extends DataOp {
    public final Map<String, Object> attrs;

    public DataOpUpdate(ReferenceInternal id,
                        Map<String, Object> attrs) {
        super(id);
        this.attrs = attrs;
    }

}
