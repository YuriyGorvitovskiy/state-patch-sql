package org.state.patch.sql.model.db;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.state.patch.sql.data.Entity;
import org.state.patch.sql.data.ReferenceInternal;
import org.state.patch.sql.model.Attribute;

public interface DatabaseLoad {

    public default Entity select(ReferenceInternal ids,
                                 Collection<Attribute> attributes) throws Exception {
        List<Entity> result = select(Collections.singleton(ids), attributes);
        return result.isEmpty() ? null : result.get(0);
    }

    public List<Entity> select(Collection<ReferenceInternal> ids,
                               Collection<Attribute> attributes) throws Exception;

    public List<Entity> select(String entityType,
                               Collection<Attribute> attributes,
                               Map<String, Object> conditions,
                               Map<String, Boolean> sorting) throws Exception;

}
