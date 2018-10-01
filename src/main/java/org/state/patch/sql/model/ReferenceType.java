package org.state.patch.sql.model;

import java.util.Objects;

/**
 * <ul>
 *  <li>JSON: string,</li>
 *  <li>Java: ReferenceInternal,</li>
 *  <li>Postgres: dictates by storageType: INTEGER or STRING</li>
 *  </ul>
 */
public class ReferenceType implements ValueType {

    public final String        entityType;
    public final PrimitiveType storageType;

    public ReferenceType(String entityType, PrimitiveType storageType) {
        this.entityType = entityType;
        this.storageType = storageType;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ReferenceType))
            return false;

        ReferenceType other = (ReferenceType) o;
        return Objects.equals(this.entityType, other.entityType) &&
               Objects.equals(this.storageType, other.storageType);
    }
}
