package org.state.patch.sql.model;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EntityType extends Traced {

    public final String                 typeName;
    public final String                 tableName;
    public final Attribute              identity;
    public final Map<String, Attribute> attrs;

    public EntityType(String typeName,
                      String tableName,
                      Attribute identity,
                      List<Attribute> attrs,
                      ReferenceExternal modifiedBy,
                      Date modifiedAt,
                      long modifiedEventId,
                      long modifiedPatchId) {

        super(modifiedBy, modifiedAt, modifiedEventId, modifiedPatchId);

        this.typeName = typeName;
        this.tableName = tableName;
        this.identity = identity;

        // To Preserve order of the attributes use LinkedHashMap
        Map<String, Attribute> ordered = new LinkedHashMap<>();
        ordered.put(identity.attribName, identity);
        for (Attribute attr : attrs) {
            ordered.put(attr.attribName, attr);
        }
        this.attrs = Collections.unmodifiableMap(ordered);
    }

    @Override
    public String toString() {
        return "EntityType (" + typeName + ")";
    }
}
