package org.state.patch.sql.model;

public class ReferenceInternal extends ReferenceAbstract {
    public static final String SEPARATOR = ":";

    public final String type;

    /**
     * To avoid unnecessary mapping we only one id should be used.
     * We will keep it in string form, until we are ready to interact with DB.
     */
    public final String id;

    public ReferenceInternal(String stringValue) {
        super(stringValue);
        String[] parts = stringValue.split(SEPARATOR);
        this.type = parts[parts.length - 2];
        this.id = parts[parts.length - 1];
    }

    public ReferenceInternal(String type, String id) {
        super(type + SEPARATOR + id);
        this.type = type;
        this.id = id;
    }
}
