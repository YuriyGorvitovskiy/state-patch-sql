package org.state.patch.sql.model.op;

import java.util.Date;
import java.util.List;

import org.state.patch.sql.data.ReferenceExternal;

public class ModelOpCreateType extends ModelOp {

    /** Each entity should have a single column identity.
     * It should be of type
     */
    public final Attribute       identity;
    public final List<Attribute> attrs;

    public ModelOpCreateType(String type,
                             Attribute identity,
                             List<Attribute> attrs,
                             ReferenceExternal issuedBy,
                             Date issuedAt,
                             long eventId,
                             long patchId) {
        super(type, issuedBy, issuedAt, eventId, patchId);
        this.identity = identity;
        this.attrs = attrs;
    }
}
