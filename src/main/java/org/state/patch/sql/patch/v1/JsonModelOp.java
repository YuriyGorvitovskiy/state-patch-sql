package org.state.patch.sql.patch.v1;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(property = "@op_type", use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
                @Type(value = JsonModelOpCreateType.class, name = "create-type"),
                @Type(value = JsonModelOpDeleteType.class, name = "delete-type"),
                @Type(value = JsonModelOpAppendAttr.class, name = "append-attr"),
                @Type(value = JsonModelOpDeleteAttr.class, name = "delete-attr"),
})
public class JsonModelOp {
    public String entity_type;
}
