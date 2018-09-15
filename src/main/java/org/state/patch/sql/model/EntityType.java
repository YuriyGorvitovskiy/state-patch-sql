package org.state.patch.sql.model;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.state.patch.sql.data.ReferenceExternal;
import org.state.patch.sql.data.Traced;

public class EntityType extends Traced {

    public final String                 name;
    public final Attribute              identity;
    public final Map<String, Attribute> attrs;

    public EntityType(String name,
                      Attribute identity,
                      List<Attribute> attrs,
                      ReferenceExternal modifiedBy,
                      Date modifiedAt,
                      long modifiedEventId,
                      long modifiedPatchId) {

        super(modifiedBy, modifiedAt, modifiedEventId, modifiedPatchId);

        this.name = name;
        this.identity = identity;

        // To Preserve order of the attributes use LinkedHashMap
        Map<String, Attribute> ordered = new LinkedHashMap<>();
        ordered.put(identity.name, identity);
        for (Attribute attr : attrs) {
            ordered.put(attr.name, attr);
        }
        this.attrs = Collections.unmodifiableMap(ordered);
    }

    @Override
    public String toString() {
        return "EntityType (" + name + ")";
    }
}
