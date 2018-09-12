package org.state.patch.sql.model.op;

import java.util.Date;
import java.util.List;

import org.state.patch.sql.model.Identity;
import org.state.patch.sql.model.ReferenceExternal;

public abstract class ModelOpCreateType extends ModelOpAbstract {

    public final String          table;
    public final Identity        identity;
    public final List<Attribute> attrs;

    protected ModelOpCreateType(String type,
                                String table,
                                Identity identity,
                                List<Attribute> attrs,
                                ReferenceExternal issuedBy,
                                Date issuedAt,
                                long eventId,
                                long patchId) {
        super(type, issuedBy, issuedAt, eventId, patchId);
        this.table = table;
        this.identity = identity;
        this.attrs = attrs;
    }
}
