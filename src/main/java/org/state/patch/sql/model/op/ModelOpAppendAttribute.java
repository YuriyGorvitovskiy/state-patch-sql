package org.state.patch.sql.model.op;

public class ModelOpAppendAttribute extends ModelOp {

    public final Attribute attr;

    public ModelOpAppendAttribute(String type, Attribute attr) {
        super(type);
        this.attr = attr;
    }
}
