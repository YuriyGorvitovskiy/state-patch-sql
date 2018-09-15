package org.state.patch.sql.data;

public class ReferenceString extends ReferenceInternal {
    /**
     * To avoid unnecessary mapping we only one id should be used.
     * We will keep it in string form, until we are ready to interact with DB.
     */
    public final String id;

    public ReferenceString(String type, String id) {
        super(type, type + SEPARATOR + id);
        this.id = id;
    }
}
