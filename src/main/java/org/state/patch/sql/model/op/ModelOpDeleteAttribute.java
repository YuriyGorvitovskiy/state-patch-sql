package org.state.patch.sql.model.op;

public class ModelOpDeleteAttribute extends ModelOp {

    public final String attribName;

    public ModelOpDeleteAttribute(String type, String attribName) {
        super(type);
        this.attribName = attribName;
    }
}
