package org.state.patch.sql.data;

public abstract class ReferenceInternal extends Reference {

    public final String type;

    public ReferenceInternal(String type, String complete) {
        super(complete);
        this.type = type;
    }
}
