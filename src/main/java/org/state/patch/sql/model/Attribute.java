package org.state.patch.sql.model;

public class Attribute {

    public final String    name;
    public final ValueType type;
    public final Object    initial;

    public Attribute(String name,
                     ValueType type,
                     Object initial) {
        this.name = name;
        this.type = type;
        this.initial = initial;
    }

    @Override
    public String toString() {
        return "Attribute (" + name + ")";
    }
}
