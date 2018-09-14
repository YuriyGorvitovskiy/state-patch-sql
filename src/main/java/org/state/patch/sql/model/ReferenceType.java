package org.state.patch.sql.model;

/**
 * <ul>
 *  <li>JSON: string,</li>
 *  <li>Java: ReferenceInternal,</li>
 *  <li>Postgres: dictates by storageType: INTEGER or STRING</li>
 *  </ul>
 */
public class ReferenceType {

    public final String        entityType;
    public final PrimitiveType storageType;

    public ReferenceType(String entityType, PrimitiveType storageType) {
        this.entityType = entityType;
        this.storageType = storageType;
    }
}
