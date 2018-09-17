package org.state.patch.sql.model.op;

import java.util.List;

public class ModelOpCreateType extends ModelOp {

    /** Each entity should have a single column identity.
     * It should be of type
     */
    public final Attribute       identity;
    public final List<Attribute> attrs;

    public ModelOpCreateType(String type,
                             Attribute identity,
                             List<Attribute> attrs) {
        super(type);
        this.identity = identity;
        this.attrs = attrs;
    }
}
