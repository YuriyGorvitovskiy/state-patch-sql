package org.state.patch.sql.model;

public enum PrimitiveType implements ValueType {

    /**
     * <ul>
     *  <li>JSON: boolean,</li>
     *  <li>Java: Boolean,</li>
     *  <li>Postgres: BOOLEAN</li>
     *  </ul>
     */
    BOOLEAN,

    /**
     * <ul>
     *  <li>JSON: number,</li>
     *  <li>Java: Integer,</li>
     *  <li>Postgres: BIGINT</li>
     *  </ul>
     */
    INTEGER,

    /**
     * <ul>
     *  <li>JSON: number,</li>
     *  <li>Java: Double,</li>
     *  <li>Postgres: DOUBLE PRECISION</li>
     *  </ul>
     */
    DOUBLE,

    /**
     * <ul>
     *  <li>JSON: string,</li>
     *  <li>Java: String,</li>
     *  <li>Postgres: CHARACTER VARYING(256)</li>
     *  </ul>
     */
    STRING,

    /**
     * <ul>
     *  <li>JSON: string,</li>
     *  <li>Java: String,</li>
     *  <li>Postgres: TEXT</li>
     *  </ul>
     */
    TEXT,

    /**
     * <ul>
     *  <li>JSON: string, ISO 8601</li>
     *  <li>Java: Date,</li>
     *  <li>Postgres: TIMESTAMP WITH TIME ZONE</li>
     *  </ul>
     */
    TIMESTAMP,

    /**
     * <ul>
     *  <li>JSON: string,</li>
     *  <li>Java: ReferenceExternal,</li>
     *  <li>Postgres: CHARACTER VARYING(256)</li>
     *  </ul>
     */
    REFERENCE_EXTERNAL
}
