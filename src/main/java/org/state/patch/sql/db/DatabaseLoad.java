package org.state.patch.sql.db;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.state.patch.sql.data.Entity;
import org.state.patch.sql.data.ReferenceInternal;
import org.state.patch.sql.model.Attribute;
import org.state.patch.sql.model.EntityType;
import org.state.patch.sql.model.Model;

public interface DatabaseLoad {

    public default Entity select(Collection<Attribute> attributes,
                                 ReferenceInternal id) throws Exception {
        if (null == id) {
            return null;
        }
        List<Entity> result = select(attributes, Collections.singleton(id));
        return result.isEmpty() ? null : result.get(0);
    }

    public default List<Entity> select(Collection<Attribute> attributes,
                                       Collection<ReferenceInternal> ids) throws Exception {
        if (null == ids || ids.isEmpty()) {
            return Collections.emptyList();
        }

        String entityTypeName = null;
        for (ReferenceInternal id : ids) {
            if (null == entityTypeName) {
                entityTypeName = id.type;
            } else if (!Objects.equals(entityTypeName, id.type)) {
                throw new RuntimeException("Mix type reference requests is not supported.");
            }
        }

        EntityType entityType = getModel().getEntityType(entityTypeName);
        if (null == entityType) {
            throw new RuntimeException("Entity type " + entityTypeName + " is not defined in model.");
        }

        Pair<Attribute, Collection<?>> condition = new ImmutablePair<>(entityType.identity, ids);
        return select(attributes,
                      entityType,
                      Collections.singleton(condition),
                      null);
    }

    public List<Entity> select(Collection<Attribute> attributes,
                               EntityType entityType,
                               Collection<Pair<Attribute, Collection<?>>> conditions,
                               Collection<Pair<Attribute, Boolean>> sortings) throws Exception;

    public Model getModel();
}
