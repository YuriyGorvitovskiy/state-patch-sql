package org.state.patch.sql.model;

import java.util.Arrays;

public class ReferenceInternal extends ReferenceAbstract {
    public static final String SEPARATOR = ":";

    public final String   type;
    public final String[] ids;

    public ReferenceInternal(String stringValue) {
        super(stringValue);
        String[] parts = stringValue.split(SEPARATOR);
        this.type = parts[0];
        this.ids = Arrays.copyOfRange(parts, 1, parts.length);
    }

    public ReferenceInternal(String type, String... ids) {
        super(type + SEPARATOR + String.join(SEPARATOR, ids));
        this.type = type;
        this.ids = ids;
    }
}
