package org.state.patch.sql.data;

import java.util.Map;

public class Entity {
    public final ReferenceInternal   id;
    public final Map<String, Object> attrs;

    public Entity(ReferenceInternal id, Map<String, Object> attrs) {
        this.id = id;
        this.attrs = attrs;
    }
}
