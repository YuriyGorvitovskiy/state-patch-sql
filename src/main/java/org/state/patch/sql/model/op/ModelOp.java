package org.state.patch.sql.model.op;

import org.state.patch.sql.model.ValueType;

public abstract class ModelOp {

    public static class Attribute {
        public final String    name;
        public final ValueType type;
        public final Object    initial;

        public Attribute(String attribName,
                         ValueType valueType,
                         Object valueInitial) {
            this.name = attribName;
            this.type = valueType;
            this.initial = valueInitial;
        }
    }

    public final String type;

    protected ModelOp(String type) {
        this.type = type;
    }
}
