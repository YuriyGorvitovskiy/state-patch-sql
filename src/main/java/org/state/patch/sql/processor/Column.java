package org.state.patch.sql.processor;

public class Column {
    public final String  name;
    public final String  type;
    public final boolean primary;

    public Column(String name, String type, boolean primary) {
        this.name = name;
        this.type = type;
        this.primary = primary;
    }
}
